import numpy as np


def linear(x, y):
    n = len(x)
    sx = sum(x); sy = sum(y)
    sx2 = sum(xi**2 for xi in x)
    sxy = sum(x[i]*y[i] for i in range(n))
    d = n*sx2 - sx**2
    a = (n*sxy - sx*sy) / d
    b = (sy*sx2 - sx*sxy) / d
    phi = [a*xi + b for xi in x]
    r = pearson(x, y)
    return {"type": "linear", "coeffs": {"a": a, "b": b}, "phi": phi, "pearson": r}


def poly2(x, y):
    coeffs = np.polyfit(x, y, 2).tolist()
    phi = np.polyval(coeffs, x).tolist()
    return {"type": "poly2", "coeffs": {"a": coeffs[0], "b": coeffs[1], "c": coeffs[2]}, "phi": phi}


def poly3(x, y):
    coeffs = np.polyfit(x, y, 3).tolist()
    phi = np.polyval(coeffs, x).tolist()
    return {"type": "poly3", "coeffs": {"a": coeffs[0], "b": coeffs[1], "c": coeffs[2], "d": coeffs[3]}, "phi": phi}


def exponential(x, y):
    if any(yi <= 0 for yi in y):
        return None
    ln_y = np.log(y)
    coeffs = np.polyfit(x, ln_y, 1)
    b = float(coeffs[0]); ln_a = float(coeffs[1])
    a = np.exp(ln_a)
    phi = [a * np.exp(b * xi) for xi in x]
    return {"type": "exponential", "coeffs": {"a": a, "b": b}, "phi": phi}


def logarithmic(x, y):
    if any(xi <= 0 for xi in x):
        return None
    ln_x = np.log(x)
    coeffs = np.polyfit(ln_x, y, 1)
    a = float(coeffs[0]); b = float(coeffs[1])
    phi = [a * np.log(xi) + b for xi in x]
    return {"type": "logarithmic", "coeffs": {"a": a, "b": b}, "phi": phi}


def power(x, y):
    if any(xi <= 0 for xi in x) or any(yi <= 0 for yi in y):
        return None
    ln_x = np.log(x); ln_y = np.log(y)
    coeffs = np.polyfit(ln_x, ln_y, 1)
    b = float(coeffs[0]); ln_a = float(coeffs[1])
    a = np.exp(ln_a)
    phi = [a * xi**b for xi in x]
    return {"type": "power", "coeffs": {"a": a, "b": b}, "phi": phi}


def pearson(x, y):
    n = len(x)
    mx = sum(x)/n; my = sum(y)/n
    num = sum((x[i]-mx)*(y[i]-my) for i in range(n))
    den = (sum((xi-mx)**2 for xi in x) * sum((yi-my)**2 for yi in y))**0.5
    return num/den if den != 0 else 0


def std_dev(y, phi):
    n = len(y)
    return (sum((phi[i]-y[i])**2 for i in range(n)) / n)**0.5


def r_squared(y, phi):
    my = sum(y)/len(y)
    ss_res = sum((y[i]-phi[i])**2 for i in range(len(y)))
    ss_tot = sum((yi-my)**2 for yi in y)
    return 1 - ss_res/ss_tot if ss_tot != 0 else 0


def r2_message(r2):
    if r2 >= 0.95:
        return "Отличная аппроксимация (R² ≥ 0.95)"
    elif r2 >= 0.75:
        return "Хорошая аппроксимация (R² ≥ 0.75)"
    elif r2 >= 0.5:
        return "Удовлетворительная аппроксимация (R² ≥ 0.5)"
    else:
        return "Неудовлетворительная аппроксимация (R² < 0.5)"


def run_all(x_raw, y_raw):
    x = np.array(x_raw, dtype=float)
    y = np.array(y_raw, dtype=float)

    candidates = [
        linear(x.tolist(), y.tolist()),
        poly2(x, y),
        poly3(x, y),
        exponential(x, y),
        logarithmic(x, y),
        power(x, y),
    ]

    results = []
    for c in candidates:
        if c is None:
            continue
        phi = c["phi"]
        s = sum((phi[i]-y_raw[i])**2 for i in range(len(y_raw)))
        sd = std_dev(y_raw, phi)
        r2 = r_squared(y_raw, phi)
        eps = [phi[i] - y_raw[i] for i in range(len(y_raw))]
        entry = {**c, "S": s, "std_dev": sd, "R2": r2, "R2_message": r2_message(r2), "eps": eps}
        if c["type"] == "linear":
            entry["pearson"] = c.get("pearson", 0)
        results.append(entry)

    if results:
        best = min(results, key=lambda r: r["std_dev"])
        for r in results:
            r["best"] = r["type"] == best["type"]

    return results
