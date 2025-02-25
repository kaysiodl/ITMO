import time
from collections.abc import Callable
from functools import partial
from pathlib import Path

from task_1 import json2yaml as manual_converter
from task_2 import json2yaml as lib_converter
from task_3 import json2yaml as regexp_converter
from task_4 import json2yaml as gram_converter

REPEATS = 100


def benchmark(func: Callable[[], str]) -> float:
    start = time.perf_counter()

    for i in range(REPEATS):
        func()

    return time.perf_counter() - start


def main() -> None:
    json_schedule = Path(__file__).parent.joinpath("timetable.json").read_text(encoding="utf-8")

    print("Без использования библиотек:", benchmark(partial(manual_converter, json_schedule)))
    print("С использование библиотек:", benchmark(partial(lib_converter, json_schedule)))
    print("С использованием регулярных выражений:", benchmark(partial(regexp_converter, json_schedule)))
    print("С использованием формальных грамматик:", benchmark(partial(gram_converter, json_schedule)))


if __name__ == "__main__":
    main()
