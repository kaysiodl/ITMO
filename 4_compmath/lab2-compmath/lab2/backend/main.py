from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, field_validator
from fastapi.middleware.cors import CORSMiddleware
import methods
import systems
import uvicorn
import math

app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

def f1(x): return x**3 + 2.28*x**2 - 1.934*x - 3.907
def f2(x): return __import__("math").sin(x) - x/2
def f3(x): return x**2 - 4
functions = {
    "f1": f1,
    "f2": f2,
    "f3": f3
}

def check_monotonicity(f, a, b):
    steps = 100
    h = (b - a) / steps
    diffs = []
    for i in range(steps):
        x_curr = a + i * h
        x_next = a + (i + 1) * h
        diffs.append(f(x_next) - f(x_curr))

    pos = any(d > 1e-12 for d in diffs)
    neg = any(d < -1e-12 for d in diffs)

    if pos and neg:
        return False
    return True

class RequestData(BaseModel):
    function: str
    method: str
    a: float
    b: float
    eps: float

    @field_validator("eps")
    def validate_eps(cls, v):
        if v <= 0:
            raise ValueError("eps должен быть > 0")
        if v < 1e-12:
            raise ValueError("eps слишком маленький (минимум 1e-12)")
        return v


@app.post("/solve")
def solve(data: RequestData):
    try:
        if data.function not in functions:
            raise HTTPException(400, "Неизвестная функция")

        f = functions[data.function]
        if f(data.a) * f(data.b) > 0:
            raise HTTPException(400, "На интервале нет корней или корней больше одного")
        if not check_monotonicity(f, data.a, data.b):
            raise HTTPException(400, "Функция не монотонна на интервале (возможно более одного корня)")
        if data.method == "chord":
            x, fx, it = methods.chord_method(f, data.a, data.b, data.eps)
        elif data.method == "secant":
            x, fx, it = methods.secant_method(f, data.a, data.b, data.eps)
        elif data.method == "iteration":
            x, fx, it = methods.simple_iteration(f, data.a, data.b, data.eps)
        else:
            raise HTTPException(400, "Неизвестный метод")
        if not math.isfinite(x) or not math.isfinite(fx):
            raise HTTPException(500, "Численный сбой")
        if x < data.a or x > data.b:
            raise HTTPException(400, "Корень найден вне интервала")
        return {"x": x, "fx": fx, "iterations": it}
    except ValueError as e:
        raise HTTPException(400, str(e))


class SystemRequestData(BaseModel):
    system: str
    x_0: float
    y_0: float
    eps: float

    @field_validator("eps")
    def validate_eps(cls, v):
        if v <= 0:
            raise ValueError("eps должен быть > 0")
        if v < 1e-12:
            raise ValueError("eps слишком маленький (минимум 1e-12)")
        return v


@app.post("/solve-system")
def solve_system(data: SystemRequestData):
    try:
        if data.system not in ("sys1", "sys2"):
            raise HTTPException(400, "Неизвестная система")

        x, y, err1, err2, iterations, path = systems.simple_iteration_system(
            data.system, data.x_0, data.y_0, data.eps
        )

        if not math.isfinite(x) or not math.isfinite(y):
            raise HTTPException(500, "Численный сбой")

        return {
            "x": x,
            "y": y,
            "err1": err1,
            "err2": err2,
            "iterations": iterations,
            "path": path
        }
    except ValueError as e:
        raise HTTPException(400, str(e))
    except Exception as e:
        raise HTTPException(500, "Метод не сошелся, перепроверьте значения")

if __name__ == '__main__':
    uvicorn.run(app, host="0.0.0.0", port=8000)