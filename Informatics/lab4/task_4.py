import re
from abc import ABC, abstractmethod
from dataclasses import dataclass
from pathlib import Path
from typing import Any


@dataclass
class IYamlItem[T](ABC):
    content: T
    indent: int = 2

    @classmethod
    def parse(cls, content: list[str]) -> "IYamlItem[T]":
        content = cls.parse_content(content[1:-1])

        return cls(content=content)

    @staticmethod
    @abstractmethod
    def parse_content(content: Any) -> T: ...

    @abstractmethod
    def to_yaml(self, depth: int = 0) -> str: ...


@dataclass
class YamlArray(IYamlItem[list]):
    @staticmethod
    def parse_content(content: list[str]) -> list[Any]:
        result = []
        i = 0
        while i < len(content):
            match content[i]:
                case "{":
                    brace_count = 0
                    j = i + 1

                    while j < len(content):
                        if content[j] in "{":
                            brace_count += 1
                        elif content[j] in "}":
                            if brace_count == 0:
                                break
                            brace_count -= 1
                        j += 1
                    result.append(YamlObject.parse(content[i:j + 1]))
                    i = j
                case "[":
                    brace_count = 0
                    j = i + 1

                    while j < len(content):
                        if content[j] in "[":
                            brace_count += 1
                        elif content[j] in "]":
                            if brace_count == 0:
                                break
                            brace_count -= 1
                        j += 1
                    result.append(YamlArray.parse(content[i:j]))
                    i = j
                case _:
                    result.append(content[i][1:-1])
            i += 1

        return result

    def to_yaml(self, depth: int = 1) -> str:
        yaml_str = ""
        for item in self.content:
            yaml_str += " " * self.indent * depth + "- "
            if isinstance(item, YamlObject):
                yaml_str += item.to_yaml(depth + 1).lstrip()
            else:
                yaml_str += item
        return yaml_str


@dataclass
class YamlObject(IYamlItem[dict]):
    @staticmethod
    def parse_content(content: Any) -> dict:
        result = {}

        i = 0
        while i < len(content):
            if is_attr(content[i]):
                attr = get_attr(content[i])

                match content[i + 1]:
                    case "{":
                        brace_count = 0
                        j = i + 2

                        while j < len(content):
                            if content[j] in "{":
                                brace_count += 1
                            elif content[j] in "}":
                                if brace_count == 0:
                                    break
                                brace_count -= 1
                            j += 1
                        result[attr] = YamlObject.parse_content(content[i + 1:j + 1])
                        i = j
                    case "[":
                        brace_count = 0
                        j = i + 2

                        while j < len(content):
                            if content[j] in "[":
                                brace_count += 1
                            elif content[j] in "]":
                                if brace_count == 0:
                                    break
                                brace_count -= 1
                            j += 1
                        result[attr] = YamlArray.parse(content[i + 1:j + 1])
                        i = j
                    case _:
                        result[attr] = content[i + 1][1:-1]
                        i += 1
            i += 1

        return result

    def to_yaml(self, depth: int = 0) -> str:
        yaml_str = ""
        for key, value in self.content.items():
            yaml_str += " " * self.indent * depth + f"{key}: "
            if isinstance(value, (YamlObject, YamlArray)):
                yaml_str += "\n"
                yaml_str += value.to_yaml(depth + 1)
            else:
                yaml_str += value
            yaml_str += "\n"

        return yaml_str


def is_attr(json_data: str) -> bool:
    return bool(re.match(r'^\s*".*"\s*$', json_data))


def get_attr(json_data: str) -> str:
    return json_data[1:-1]


def split_into_tokens(json_data: str) -> list[str]:
    return re.findall(r'(\{|}|\[|]|"[^"]*"|\d+|[a-zA-Z_][a-zA-Z0-9_]*)', json_data)


def json2yaml(json_data: str) -> str:
    tokens = split_into_tokens(json_data)
    yaml_object = YamlObject.parse(tokens).to_yaml()

    return yaml_object


def main() -> None:
    json_path = Path(__file__).parent.joinpath("timetable.json")
    yaml_path = Path(__file__).parent.joinpath("timetable_4.yaml")

    json_schedule = json_path.read_text(encoding="utf-8")
    yaml_schedule = json2yaml(json_schedule)

    yaml_path.write_text(yaml_schedule, encoding="utf-8")


if __name__ == "__main__":
    main()
