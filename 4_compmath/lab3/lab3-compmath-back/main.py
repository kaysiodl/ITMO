from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from typing import List, Optional
import math

app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)

class MathFunctions:
    @staticmethod
    def get_function(eq_id: int):
        functions = {
            0: lambda x: 4*x**3 - 5*x**2 + 6*x - 7,
            1: lambda x: math.sin(x**2) + 0.5,
            2: lambda x: math.exp(-x**2) * 5,
            3: lambda x: (x**2 + 1) / (x + 2) if x != -2 else 0,
            4: lambda x: math.cos(x) / (x + 5)
        }
        return functions.get(eq_id)

    @staticmethod
    def check_convergence(f, a, b, eps):
        critical_points = [a, b]
        steps = 100
        for i in range(1, steps):
            p = a + (b - a) * i / steps
            try:
                val = f(p)
                if math.isinf(val) or math.isnan(val):
                    critical_points.append(p)
            except ZeroDivisionError:
                critical_points.append(p)
        for x0 in critical_points:
            try:
                direction = 1 if x0 == a else -1
                if x0 != a and x0 != b: direction = 0
                delta = 1e-5
                test_points = []
                if direction >= 0: test_points.append(x0 + delta)
                if direction <= 0: test_points.append(x0 - delta)
                is_bad = False
                for tp in test_points:
                    if math.isinf(f(tp)) or abs(f(tp)) > 1 / eps:
                        is_bad = True
                        break
                if not is_bad:
                    continue

EQUATIONS = [
    {"id": 0, "formula": "4*x^3 - 5*x^2 + 6*x - 7", "view": "4x³ - 5x² + 6x - 7"},
    {"id": 1, "formula": "sin(x^2) + 0.5", "view": "sin(x²) + 0.5"},
    {"id": 2, "formula": "5 * exp(-x^2)", "view": "5e^{-x²}"},
    {"id": 3, "formula": "(x^2 + 1)/(x + 2)", "view": "(x² + 1)/(x + 2)"},
    {"id": 4, "formula": "cos(x)/(x + 5)", "view": "cos(x)/(x + 5)"}
]
class IntegrationMethods:
    @staticmethod
    def left_rectangles(f, a, b, n):
        h = (b - a) / n
        return sum(f(a + i * h) for i in range(n)) * h

    @staticmethod
    def right_rectangles(f, a, b, n):
        h = (b - a) / n
        return sum(f(a + i * h) for i in range(1, n + 1)) * h

    @staticmethod
    def middle_rectangles(f, a, b, n):
        h = (b - a) / n
        return sum(f(a + i * h + h / 2) for i in range(n)) * h

    @staticmethod
    def trapezoidal(f, a, b, n):
        h = (b - a) / n
        s = (f(a) + f(b)) / 2
        for i in range(1, n):
            s += f(a + i * h)
        return s * h

    @staticmethod
    def simpson(f, a, b, n):
        if n % 2 != 0: n += 1
        h = (b - a) / n
        s = f(a) + f(b)
        for i in range(1, n):
            k = 4 if i % 2 != 0 else 2
            s += k * f(a + i * h)
        return s * (h / 3)



METHODS = [
    {"id": 0, "label": "Левые прямоугольники", "order": 1},
    {"id": 1, "label": "Правые прямоугольники", "order": 1},
    {"id": 2, "label": "Средние прямоугольники", "order": 2},
    {"id": 3, "label": "Метод трапеций", "order": 2},
    {"id": 4, "label": "Метод Симпсона", "order": 4}
]


class SolveRequest(BaseModel):
    equationId: int
    methodId: int
    a: float
    b: float
    eps: float


@app.get("/lab3/api/integrals/equations")
def get_equations():
    return EQUATIONS


@app.get("/lab3/api/integrals/methods")
def get_methods():
    return METHODS


@app.post("/lab3/api/integrals/solve")
def solve(req: SolveRequest):
    f = MathFunctions.get_function(req.equationId)
    if f is None: raise HTTPException(status_code=400, detail="Функция не найдена")
    methods_map = {
        0: (IntegrationMethods.left_rectangles, 1),
        1: (IntegrationMethods.right_rectangles, 1),
        2: (IntegrationMethods.middle_rectangles, 2),
        3: (IntegrationMethods.trapezoidal, 2),
        4: (IntegrationMethods.simpson, 4)
    }
    method_func, p = methods_map.get(req.methodId)
    n = 4
    iter_limit = 10000000
    while n <= iter_limit:
        i1 = method_func(f, req.a, req.b, n)
        i2 = method_func(f, req.a, req.b, n * 2)
        runge_error = abs(i2 - i1) / (2 ** p - 1)
        if runge_error <= req.eps:
            return {
                "value": f"{i2:.10f}",
                "splits": n * 2,
                "error": None
            }
        n *= 2
    return {"value": None, "splits": n, "error": "Превышено число итераций. Поменяйте введенные данные."}


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="localhost", port=8080)