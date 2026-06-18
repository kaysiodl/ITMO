import math


def make_grid(x0, xn, h):
    n = max(1, round((xn - x0) / h))
    return [x0 + i * h for i in range(n + 1)]


EQUATIONS = {
    'y_prime_y': {
        'label': "y' = y",
        'expr': "y' = y",
        'f': lambda x, y: y,
        'exact': lambda x, x0, y0: y0 * math.exp(x - x0),
    },
    'y_prime_x_plus_y': {
        'label': "y' = x + y",
        'expr': "y' = x + y",
        'f': lambda x, y: x + y,
        'exact': lambda x, x0, y0: (y0 + x0 + 1) * math.exp(x - x0) - x - 1,
    },
    'y_prime_y_minus_x2_plus_1': {
        'label': "y' = y - x² + 1",
        'expr': "y' = y - x^2 + 1",
        'f': lambda x, y: y - x * x + 1,
        'exact': lambda x, x0, y0: (x + 1) ** 2 + (y0 - (x0 + 1) ** 2) * math.exp(x - x0),
    },
    'y_prime_x_minus_y': {
        'label': "y' = x - y",
        'expr': "y' = x - y",
        'f': lambda x, y: x - y,
        'exact': lambda x, x0, y0: x - 1 + (y0 - x0 + 1) * math.exp(x0 - x),
    },
    'y_prime_x_times_y': {
        'label': "y' = x·y",
        'expr': "y' = x*y",
        'f': lambda x, y: x * y,
        'exact': lambda x, x0, y0: y0 * math.exp((x * x - x0 * x0) / 2.0),
    },
}


def euler(f, x0, y0, xn, h):
    xs = make_grid(x0, xn, h)
    ys = [y0]
    for i in range(len(xs) - 1):
        ys.append(ys[i] + h * f(xs[i], ys[i]))
    return xs, ys


def improved_euler(f, x0, y0, xn, h):
    xs = make_grid(x0, xn, h)
    ys = [y0]
    for i in range(len(xs) - 1):
        fi = f(xs[i], ys[i])
        y_predict = ys[i] + h * fi
        ys.append(ys[i] + h / 2.0 * (fi + f(xs[i] + h, y_predict)))
    return xs, ys


def _rk4_step(f, x, y, h):
    k1 = h * f(x, y)
    k2 = h * f(x + h / 2.0, y + k1 / 2.0)
    k3 = h * f(x + h / 2.0, y + k2 / 2.0)
    k4 = h * f(x + h, y + k3)
    return y + (k1 + 2.0 * k2 + 2.0 * k3 + k4) / 6.0


def milne(f, x0, y0, xn, h, eps):
    xs = make_grid(x0, xn, h)
    ys = [y0]
    fs = [f(xs[0], ys[0])]

    start = min(3, len(xs) - 1)
    for i in range(start):
        y_next = _rk4_step(f, xs[i], ys[i], h)
        ys.append(y_next)
        fs.append(f(xs[i + 1], y_next))

    for i in range(4, len(xs)):
        y_pred = ys[i - 4] + 4.0 * h / 3.0 * (2.0 * fs[i - 3] - fs[i - 2] + 2.0 * fs[i - 1])
        f_pred = f(xs[i], y_pred)
        y_corr = ys[i - 2] + h / 3.0 * (fs[i - 2] + 4.0 * fs[i - 1] + f_pred)
        for _ in range(50):
            f_corr = f(xs[i], y_corr)
            y_new = ys[i - 2] + h / 3.0 * (fs[i - 2] + 4.0 * fs[i - 1] + f_corr)
            if abs(y_new - y_corr) < eps:
                y_corr = y_new
                break
            y_corr = y_new
        ys.append(y_corr)
        fs.append(f(xs[i], y_corr))
    return xs, ys


def runge_error(solver, f, x0, y0, xn, h, order):
    xs_h, ys_h = solver(f, x0, y0, xn, h)
    _, ys_half = solver(f, x0, y0, xn, h / 2.0)
    denom = 2.0 ** order - 1.0
    errors = [abs(ys_h[i] - ys_half[2 * i]) / denom for i in range(len(xs_h))]
    return errors, max(errors)


def exact_solution(equation_key, xs, x0, y0):
    exact = EQUATIONS[equation_key]['exact']
    return [exact(x, x0, y0) for x in xs]


def max_exact_error(ys, ys_exact):
    return max(abs(ya - ye) for ya, ye in zip(ys, ys_exact))
