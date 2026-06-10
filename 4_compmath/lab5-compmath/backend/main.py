from flask import Flask, request, jsonify
from flask_cors import CORS
import os
import interpolation as interp

app = Flask(__name__)
CORS(app)

DATA_DIR = os.path.join(os.path.dirname(__file__), 'data')

METHOD_FNS = {
    'lagrange': interp.lagrange,
    'newton_div_forward': interp.newton_divided_forward,
    'newton_div_backward': interp.newton_divided_backward,
    'newton_fin_forward': interp.newton_finite_forward,
    'newton_fin_backward': interp.newton_finite_backward,
    'gauss_forward': interp.gauss_forward,
    'gauss_backward': interp.gauss_backward,
}

EQUIDISTANT_ONLY = {
    'newton_fin_forward', 'newton_fin_backward',
    'gauss_forward', 'gauss_backward',
}


@app.route('/api/files', methods=['GET'])
def list_files():
    files = sorted(f for f in os.listdir(DATA_DIR) if f.endswith('.txt'))
    return jsonify(files)


@app.route('/api/load_file', methods=['POST'])
def load_file():
    data = request.get_json()
    filename = data.get('filename', '')
    filepath = os.path.join(DATA_DIR, os.path.basename(filename))
    if not os.path.exists(filepath):
        return jsonify({'error': 'Файл не найден'}), 404
    xs, ys = [], []
    with open(filepath, encoding='utf-8') as f:
        for line in f:
            line = line.strip()
            if not line or line.startswith('#'):
                continue
            parts = line.replace(',', '.').split()
            if len(parts) >= 2:
                try:
                    xs.append(float(parts[0]))
                    ys.append(float(parts[1]))
                except ValueError:
                    pass
    return jsonify({'xs': xs, 'ys': ys})


@app.route('/api/interpolate', methods=['POST'])
def interpolate():
    data = request.get_json()
    try:
        xs = [float(v) for v in data['xs']]
        ys = [float(v) for v in data['ys']]
        x_target = float(data['x_target'])
    except (KeyError, ValueError, TypeError):
        return jsonify({'error': 'Некорректные входные данные'}), 400

    if len(xs) < 2:
        return jsonify({'error': 'Нужно минимум 2 точки'}), 400
    if len(xs) != len(ys):
        return jsonify({'error': 'Количество X и Y должно совпадать'}), 400
    if len(set(xs)) != len(xs):
        return jsonify({'error': 'Значения X должны быть уникальными'}), 400

    methods = data.get('methods', list(METHOD_FNS.keys()))
    equidistant = interp.is_equidistant(xs)

    diff_table = None
    if equidistant:
        raw = interp.finite_differences(ys)
        diff_table = [[round(v, 8) for v in row] for row in raw]

    div_diff_raw = interp.divided_differences(xs, ys)
    div_diff_table = [[round(v, 8) for v in row] for row in div_diff_raw]

    def passes_nodes(fn):
        for xi, yi in zip(xs, ys):
            try:
                val = fn(xs, ys, xi)
                if val is None or abs(val - yi) > 1e-4:
                    return False
            except Exception:
                return False
        return True

    results = {}
    for method in methods:
        if method not in METHOD_FNS:
            continue
        if not equidistant and method in EQUIDISTANT_ONLY:
            results[method] = None
            continue
        fn = METHOD_FNS[method]
        if not passes_nodes(fn):
            results[method] = None
            continue
        try:
            val = fn(xs, ys, x_target)
            results[method] = round(val, 8) if val is not None else None
        except Exception:
            results[method] = None

    x_min, x_max = min(xs), max(xs)
    chart_xs = [x_min + (x_max - x_min) * i / 199 for i in range(200)]

    chart_data = {}
    for method in methods:
        if method not in METHOD_FNS:
            continue
        if results.get(method) is None:
            continue
        fn = METHOD_FNS[method]
        pts = []
        for cx in chart_xs:
            try:
                pts.append(round(fn(xs, ys, cx), 8))
            except Exception:
                pts.append(None)
        chart_data[method] = pts

    return jsonify({
        'results': results,
        'equidistant': equidistant,
        'diff_table': diff_table,
        'div_diff_table': div_diff_table,
        'chart_xs': chart_xs,
        'chart_data': chart_data,
        'xs': xs,
        'ys': ys,
    })


if __name__ == '__main__':
    app.run(debug=True, port=8000)
