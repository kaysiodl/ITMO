import json
from pathlib import Path

import yaml


def json2yaml(json_data: str) -> str:
    parsed_data = json.loads(json_data)
    yaml_data = yaml.dump(parsed_data, allow_unicode=True, sort_keys=False)

    return yaml_data


def main() -> None:
    json_path = Path(__file__).parent.joinpath("timetable.json")
    yaml_path = Path(__file__).parent.joinpath("timetable_2.yaml")

    json_schedule = json_path.read_text(encoding="utf-8")
    yaml_schedule = json2yaml(json_schedule)

    yaml_path.write_text(yaml_schedule, encoding="utf-8")


if __name__ == "__main__":
    main()
