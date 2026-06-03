from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, field_validator
from typing import List
from methods import run_all

app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)


class PointsInput(BaseModel):
    x: List[float]
    y: List[float]

    @field_validator("x", "y")
    @classmethod
    def check_length(cls, v):
        if len(v) < 8 or len(v) > 12:
            raise ValueError("Количество точек должно быть от 8 до 12")
        return v

    @field_validator("y")
    @classmethod
    def check_same_length(cls, v, info):
        if "x" in info.data and len(v) != len(info.data["x"]):
            raise ValueError("Количество x и y должно совпадать")
        return v

    @field_validator("x")
    @classmethod
    def check_unique_x(cls, v):
        if len(set(v)) != len(v):
            raise ValueError("Значения x должны быть уникальными")
        return v


@app.post("/approximate")
def approximate(data: PointsInput):
    try:
        results = run_all(data.x, data.y)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
    return {"x": data.x, "y": data.y, "results": results}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="localhost", port=8000)