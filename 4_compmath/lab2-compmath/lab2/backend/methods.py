import math


def f1(x):
    return x ** 3 + 2.28 * x ** 2 - 1.934 * x - 3.907


def f2(x):
    return math.sin(x) - x / 2


def f3(x):
    return x ** 2 - 4


def chord_method(f, a, b, eps, max_iter=1000):
    iteration = 0
    x = 0
    if f(a) * f(b) > 0:
        raise ValueError("На интервале нет корней или корней больше одного")
    while iteration < max_iter:
        x = a - f(a) * (b - a) / (f(b) - f(a))
        if abs(f(x)) < eps:
            return x, f(x), iteration
        if f(a) * f(x) < 0:
            b = x
        else:
            a = x
        iteration += 1
    return x, f(x), iteration


def secant_method(f, x0, x1, eps, max_iter=1000):
    iteration = 0
    a, b = x0, x1
    if f(a) * f(b) > 0:
        raise ValueError("На интервале нет корней или корней больше одного")
    while iteration < max_iter:
        f0, f1_val = f(x0), f(x1)
        if abs(f1_val - f0) < 1e-12:
            raise ValueError("Деление на ноляь")
        x2 = x1 - f1_val * (x1 - x0) / (f1_val - f0)
        if abs(x2 - x1) < eps:
            if x2 < a or x2 > b:
                raise ValueError("На интервале нет корней или корней больше одного")
            return x2, f(x2), iteration + 1
        x0, x1 = x1, x2
        iteration += 1
    if x2 < a or x2 > b or abs(f(x2)) > eps:
        raise ValueError("Метод секущих не сошёлся или корня нет в интервале")
    return x2, f(x2), iteration


def get_derivative(f, x, h=1e-6):
    return (f(x + h) - f(x - h)) / (2 * h)


def simple_iteration(f, a, b, eps):
    steps = 10
    derivatives = [get_derivative(f, a + (b - a) * i / steps) for i in range(steps + 1)]
    f_prime_min = min(derivatives)
    f_prime_max = max(derivatives)
    max_abs_f_prime = max(abs(f_prime_min), abs(f_prime_max))
    sign = 1 if f_prime_max > 0 else -1
    lam = sign / max_abs_f_prime
    x = (a + b) / 2
    iterations = 0
    max_iters = 1000
    for f_prime in derivatives:
        phi_prime = 1 - lam * f_prime
        if abs(phi_prime) >= 1:
            raise ValueError(f"Внимание, условие сходимости не выполнено!! |phi'| = {abs(phi_prime):.4f}")
    while True:
        x_new = x - lam * f(x)
        iterations += 1
        if abs(x_new - x) < eps:
            return x_new, f(x_new), iterations
        if iterations > max_iters:
            raise ValueError(f"Метод не сошелся за 1000 итераций. "
                             f"Возможно, не выполнено условие |phi'(x)| < 1 на этом отрезке.")
        x = x_new
