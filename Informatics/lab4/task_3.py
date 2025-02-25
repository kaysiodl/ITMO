import re
from pathlib import Path

IS_JSON_OBJECT_PATTERN = re.compile(r"^{.*}$", flags=re.DOTALL)
IS_JSON_ARRAY_PATTERN = re.compile(r"^\s*\[.*]\s*$", flags=re.DOTALL)
IS_JSON_STRING_PATTERN = re.compile(r'^\s*".*"\s*$', flags=re.DOTALL)


def parse_json(json_string: str):
    json_string = json_string.strip()

    if IS_JSON_OBJECT_PATTERN.match(json_string):
        items = re.findall(
            r'"([^"]+)":\s*(\[[^]]*]|\{.*?}|".*?"|\d+)',
            json_string,
            flags=re.DOTALL
        )
        obj = {}
        for key, value in items:
            key = key.strip().strip('"')
            value = parse_json(value.strip())
            obj[key] = value
        return obj
    elif IS_JSON_ARRAY_PATTERN.match(json_string):
        items = re.findall(r"{.*?}", json_string, flags=re.DOTALL)
        return [parse_json(item.strip()) for item in items]
    elif IS_JSON_STRING_PATTERN.match(json_string):
        return json_string.strip('"')
    elif json_string == "true":
        return True
    elif json_string == "false":
        return False
    elif json_string == "null":
        return None
    else:
        try:
            if "." in json_string:
                return float(json_string)
            else:
                return int(json_string)
        except ValueError:
            raise ValueError(f"Invalid JSON value: {json_string}")


def convert_to_yaml(data, indent: int = 0) -> str:
    yaml_string = ""
    indent_space = "  " * indent

    if isinstance(data, dict):
        for key, value in data.items():
            if isinstance(value, (dict, list)):
                yaml_string += f"{indent_space}{key}:\n"
                yaml_string += convert_to_yaml(value, indent + 1)
            else:
                yaml_string += f"{indent_space}{key}: {value}\n"
    elif isinstance(data, list):
        for value in data:
            if isinstance(value, dict):
                yaml_string += f"{indent_space}- "
                yaml_string += convert_to_yaml(value, indent + 1).lstrip()
            else:
                yaml_string += f"{indent_space}- {value}\n"

    return yaml_string


def json2yaml(json_data: str) -> str:
    parsed_data = parse_json(json_data)
    yaml_data = convert_to_yaml(parsed_data)

    return yaml_data


def main() -> None:
    json_path = Path(__file__).parent.joinpath("timetable.json")
    yaml_path = Path(__file__).parent.joinpath("timetable_3.yaml")

    json_schedule = json_path.read_text(encoding="utf-8")
    yaml_schedule = json2yaml(json_schedule)

    yaml_path.write_text(yaml_schedule, encoding="utf-8")


if __name__ == "__main__":
    main()
