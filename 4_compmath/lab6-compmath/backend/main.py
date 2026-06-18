from flask import Flask, request, jsonify
from flask_cors import CORS
import ode_methods as ode

app = Flask(__name__)
CORS(app)

ONE_STEP_METHODS = {
    'euler': {'solver': ode.euler, 'order': 1},
    'improved_euler': {'solver': ode.improved_euler, 'order': 2},
}
MULTI_STEP_METHODS = {'milne'}
ALL_METHODS = set(ONE_STEP_METHODS) | MULTI_STEP_METHODS


@app.route('/api/equations', methods=['GET'])
def list_equations():
    return jsonify([
        {'key': key, 'label': data['label'], 'expr': data['expr']}
        for key, data in ode.EQUATIONS.items()
    ])


@app.route('/api/solve', methods=['POST'])
def solve():
    data = request.get_json(silent=True) or {}

    equation_key = data.get('equation')
    if equation_key not in ode.EQUATIONS:
        return jsonify({'error': 'Уравнение не выбрано или не поддерживается'}), 400

    try:
        x0 = float(data['x0'])
        y0 = float(data['y0'])
        xn = float(data['xn'])
        h = float(data['h'])
        eps = float(data['eps'])
    except (KeyError, ValueError, TypeError):
        return jsonify({'error': 'Некорректные числовые данные'}), 400

    if xn <= x0:
        return jsonify({'error': 'Конец интервала xn должен быть больше x0'}), 400
    if h <= 0:
        return jsonify({'error': 'Шаг h должен быть положительным'}), 400
    if h > (xn - x0):
        return jsonify({'error': 'Шаг h не может превышать длину интервала'}), 400
    if eps <= 0:
        return jsonify({'error': 'Точность ε должна быть положительной'}), 400

    selected = data.get('methods', list(ALL_METHODS))
    selected = [m for m in selected if m in ALL_METHODS]
    if not selected:
        return jsonify({'error': 'Выберите хотя бы один метод'}), 400

    f = ode.EQUATIONS[equation_key]['f']
    n_intervals = max(1, round((xn - x0) / h))

    try:
        xs = ode.make_grid(x0, xn, h)
        exact_nodes = ode.exact_solution(equation_key, xs, x0, y0)
    except (ValueError, OverflowError):
        return jsonify({'error': 'Ошибка вычисления (проверьте параметры)'}), 400

    methods_result = {}
    for key in selected:
        try:
            if key in ONE_STEP_METHODS:
                cfg = ONE_STEP_METHODS[key]
                _, ys = cfg['solver'](f, x0, y0, xn, h)
                runge_nodes, runge_max = ode.runge_error(
                    cfg['solver'], f, x0, y0, xn, h, cfg['order'])
                methods_result[key] = {
                    'kind': 'one_step',
                    'order': cfg['order'],
                    'ys': [round(v, 8) for v in ys],
                    'runge_nodes': [round(v, 10) for v in runge_nodes],
                    'runge_max': round(runge_max, 10),
                    'runge_ok': runge_max <= eps,
                }
            else:
                if n_intervals < 4:
                    methods_result[key] = {
                        'kind': 'multi_step',
                        'ys': None,
                        'note': 'Нужно не менее 4 шагов (узлов ≥ 5) для метода Милна',
                    }
                    continue
                _, ys = ode.milne(f, x0, y0, xn, h, eps)
                max_err = ode.max_exact_error(ys, exact_nodes)
                methods_result[key] = {
                    'kind': 'multi_step',
                    'order': 4,
                    'ys': [round(v, 8) for v in ys],
                    'max_error': round(max_err, 10),
                    'max_error_ok': max_err <= eps,
                }
        except (ValueError, OverflowError, ZeroDivisionError):
            methods_result[key] = {'ys': None, 'note': 'Ошибка вычисления метода'}

    dense_n = 300
    xs_dense = [x0 + (xn - x0) * i / (dense_n - 1) for i in range(dense_n)]
    try:
        exact_dense = ode.exact_solution(equation_key, xs_dense, x0, y0)
    except (ValueError, OverflowError):
        xs_dense, exact_dense = xs, exact_nodes

    return jsonify({
        'equation': ode.EQUATIONS[equation_key]['expr'],
        'xs': [round(v, 8) for v in xs],
        'exact': [round(v, 8) for v in exact_nodes],
        'methods': methods_result,
        'chart': {
            'xs_dense': [round(v, 8) for v in xs_dense],
            'exact_dense': [round(v, 8) for v in exact_dense],
        },
    })


if __name__ == '__main__':
    app.run(debug=True, port=8000)
