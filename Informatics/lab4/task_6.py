import csv
import json
from io import StringIO
from pathlib import Path


def json2csv(json_schedule: str) -> str:
    json_data = json.loads(json_schedule)
    io = StringIO()
    writer = csv.writer(io)

    headers = [
        "weekday",
        "lesson_type",
        "lesson_name",
        "room",
        "teacher",
        "time_start",
        "time_end"
    ]
    writer.writerow(headers)

    for weekday, lessons in json_data.items():
        for lesson in lessons:
            lesson_type = lesson["type"]
            lesson_name = lesson["name"]
            room = lesson["room"]
            teacher = lesson["teacher"]
            time_start = lesson["time_start"]
            time_end = lesson["time_end"]

            writer.writerow([
                weekday,
                lesson_type,
                lesson_name,
                room,
                teacher,
                time_start,
                time_end
            ])

    return io.getvalue()


def main() -> None:
    json_path = Path(__file__).parent.joinpath("timetable.json")
    csv_path = Path(__file__).parent.joinpath("timetable.csv")

    json_schedule = json_path.read_text(encoding="utf-8")
    csv_schedule = json2csv(json_schedule)

    csv_path.write_text(csv_schedule, encoding="utf-8", newline="")


if __name__ == "__main__":
    main()
