from pathlib import Path


def json_is_object(json_string: str) -> bool:
    return json_string.startswith("{") and json_string.endswith("}")


def json_is_array(json_string: str) -> bool:
    return json_string.startswith("[") and json_string.endswith("]")


def json_is_string(json_string: str) -> bool:
    return json_string.startswith('"') and json_string.endswith('"')


def parse_json(json_string: str):
    json_string = json_string.strip()

    if json_is_object(json_string) or json_is_array(json_string):
        items = []
        current = ""
        inside_string = False
        brace_count = 0

        for char in json_string[1:-1]:
            if char == '"' and (len(current) == 0 or current[-1] != '\\'):
                inside_string = not inside_string
            if char in "{[":
                brace_count += 1
            elif char in "}]":
                brace_count -= 1
            if char == ',' and not inside_string and brace_count == 0:
                items.append(current.strip())
                current = ""
            else:
                current += char
        if current:
            items.append(current.strip())

        if json_is_object(json_string):
            obj = {}
            for item in items:
                key, value = item.split(":", 1)
                key = key.strip().strip('"')
                value = parse_json(value.strip())
                obj[key] = value
            return obj
        else:
            arr = []
            for item in items:
                arr.append(parse_json(item.strip()))
            return arr

    elif json_is_string(json_string):
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
    yaml_path = Path(__file__).parent.joinpath("timetable_1.yaml")

    json_schedule = json_path.read_text(encoding="utf-8")
    yaml_schedule = json2yaml(json_schedule)

    yaml_path.write_text(yaml_schedule, encoding="utf-8")


if __name__ == "__main__":
    main()
