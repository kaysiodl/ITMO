import math

def sys1_original(x, y):
    return x - math.cos(y) - 0.5, y - math.sin(x) + 0.3

def sys2_original(x, y):
    return x**2 + y - 2, x + y**2 - 2


# def check_jacobian_convergence(func, x, y, lam1, lam2):
#     d1_dx, d1_dy = get_derivatives(0, func, x, y)
#     d2_dx, d2_dy = get_derivatives(1, func, x, y)
#     phi1_dx = abs(1 - lam1 * d1_dx)
#     phi1_dy = abs(-lam1 * d1_dy)
#     phi2_dx = abs(-lam2 * d2_dx)
#     phi2_dy = abs(1 - lam2 * d2_dy)
#     row1 = phi1_dx + phi1_dy
#     row2 = phi2_dx + phi2_dy
#     norm = max(row1, row2)
#     return norm < 1, norm

def get_derivatives(f_index, sys_func, x, y, h=1e-6):
    df_dx = (sys_func(x + h, y)[f_index] - sys_func(x - h, y)[f_index]) / (2 * h)
    df_dy = (sys_func(x, y + h)[f_index] - sys_func(x, y - h)[f_index]) / (2 * h)
    return df_dx, df_dy

def simple_iteration_system(sys_name, x_0, y_0, eps):
    systems = {
        'sys1': sys1_original,
        'sys2': sys2_original,
    }
    func = systems[sys_name]
    d1_dx, d1_dy = get_derivatives(0, func, x_0, y_0)
    d2_dx, d2_dy = get_derivatives(1, func, x_0, y_0)
    lam1 = 1.0 / d1_dx if d1_dx != 0 else 0.1
    lam2 = 1.0 / d2_dy if d2_dy != 0 else 0.1
    x, y = x_0, y_0
    iterations = 0
    path = []
    while True:
        # flag, norm = check_jacobian_convergence(func, x, y, lam1, lam2)
        # if not flag:
        #     raise ValueError(f"Метод не сходтся, норма матрицы Якоби = {norm:.2f}")
        f1, f2 = func(x, y)
        x_new = x - lam1 * f1
        y_new = y - lam2 * f2
        iterations += 1
        err1 = abs(x_new - x)
        err2 = abs(y_new - y)
        path.append((x_new, y_new))
        x, y = x_new, y_new
        if max(err1, err2) < eps:
            break
        if iterations > 100000:
            raise ValueError("Метод не сошёлся за 100000 итераций")
    return x, y, err1, err2, iterations, path

