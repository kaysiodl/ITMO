import re
from collections import Counter


def adjectives(k, text):
    k = int(k.split()[1])
    repeated = re.findall(r"\b(\w+)(?:ая|яя|ое|ее|ие|ые|ого|его|ому|ему|ом|ем|их|ых|ими|ыми|им|ым|ую|юю|щий|ей|ый)\b", text)
    adjective_pattern = Counter(repeated).most_common(1)[0][0]

    adjectives = re.findall(
        r"\b{adjective_pattern}\w+\b".format(adjective_pattern=adjective_pattern), text,
        flags=re.IGNORECASE
    )
    needed_form = adjectives[k-1]

    replaced_text = re.sub(
        pattern=r"\b{adjective_pattern}\w+\b".format(adjective_pattern=adjective_pattern),
        repl=needed_form,
        string=text,
        flags=re.IGNORECASE
    )

    return replaced_text



#Тест 1
print(adjectives("Номер 2", """Футбольный клуб «Реал Мадрид» является 15-кратным обладателем главного
футбольного европейского трофея – Лиги Чемпионов. Данный турнир организован
Союзом европейских футбольных ассоциаций (УЕФА). Идея о континентальном
футбольном турнире пришла к журналисту Габриэлю Ано в 1955 году."""))

#Тест 2
print(adjectives("Номер 4", "Красивая девушка в красивых сапогах увидела в небе красивых птиц и красивый самолет"))

#Тест 3
print(adjectives("Номер 1", "Я видел высокое здание возле высоких деревьев и под высокими облаками."))

#Тест 4
print(adjectives("Номер 2", "Я люблю читать интересные истории о интересных людях в интересной стране."))

#Тест 5
print(adjectives("Номер 3", "Большой дом стоит на большом холме, рядом с большим лесом."))























































##import re
##
##def encrypt_numbers(text):
##    #Вычислим значения (a= 4*x^2 - 7)
##    def encrypt(match):
##        x = int(match.group())
##        return str(4 * (x ** 2) - 7)
##
##    #Замена всех целых чисел в тексте
##    encrypt_text = re.sub(r'-?\d+', encrypt, text)
##    return encrypt_text
##
##
###Тест 1
###Ввод: Корни данного уравнения: 28, 726 и -52.
###Вывод: Корни данного уравнения: 3129, 2108297 и 10809.
##print(encrypt_numbers("Корни данного уравнения: 28, 726 и -52."))
##
###Тест 2
###Ввод: У нас сегодня 3 опоздавших и 5 болеющих!
###Вывод: У нас сегодня 29 опоздавших и 93 болеющих!
##print(encrypt_numbers("У нас сегодня 3 опоздавших и 5 болеющих!"))
##
###Тест 3
###Ввод: История Санкт-Петербурга началась 27 (16 по старому стилю) мая 1703 года.
###Вывод: История Санкт-Петербурга началась 2909 (1017 по старому стилю) мая 11600829 года.
##print(encrypt_numbers("История Санкт-Петербурга началась 27 (16 по старому стилю) мая 1703 года"))
##
###Тест 4
###Ввод: Множество содержит числа: 1, 3, 5, 7, 9, 11, 13, 15.
###Вывод: Множество содержит числа: -3, 29, 93, 189, 317, 477, 669, 893.
##print(encrypt_numbers("Множество содержит числа: 1, 3, 5, 7, 9, 11, 13, 15."))
##
###Тест 5
###Ввод: Пётр I правил с 1682 по 1725 год.
###Вывод: Пётр I правил с 11316489 по 11902493 год.
##print(encrypt_numbers("Пётр I правил с 1682 по 1725 год."))



##import re
##
##def Is_haiku(text):
##    pattern1 = r'/'
##    pattern2 = r'[уеыаоэяиюёУЕЫАОЭЯИЮЁeyuioaEYUIOA]'
##    k = len(re.findall(pattern1, text))
##    if k!=2:
##        return "Не хайку. Должно быть три строки."
##    lines = text.split('/')
##    if len(re.findall(pattern2, lines[0]))==5 and len(re.findall(pattern2, lines[1]))==7\
##       and len(re.findall(pattern2, lines[2]))==5:
##        return "Хайку!"
##    return "Не хайку."
## 
##
##
###Тест 1
###Вывод: Хайку!
##print(Is_haiku("На мертвой ветке/ Ворон сидит, одинок./Осенний вечер."))
##
###Тест 2
###Вывод: Не хайку. Должно быть три строки.
##print(Is_haiku("Я Вас любил...."))
##
###Тест 3
###Вывод: Не хайку.
##print(Is_haiku("В минуту жизни трудную/Теснится ль в сердце грусть,/Одну молитву чудную твержу я наизусть."))
##
###Тест 4
###Вывод: Не хайку!
##print(Is_haiku("Morning cloudiness/ cotton picking my boyhood/ ahead to the past"))
##
###Тест 5
###Вывод: Хайку!
##print(Is_haiku("slow moving river/ flash of iridescent black/ kingfisher strikes")
##





##
##import re
##
##def count_smile(text):
##    return len(re.findall(r'8-{O', text))
##
##
###Тест 1
##print(count_smile('$%:-):>{)X-O:-{(8-{OXX;<=-{('))
###Ответ: 1
##
##
###Тест 2
##print(count_smile('Здравствуйте ;-) , введите смайл(Пример: 8-{O) :'))
###Ответ: 1
##
##
###Тест 3
##print(count_smile('*:-);{(:-{{(X-/$%:-):>{)X-O:-)X<O'))
###Ответ: 0
##
##
###Тест 4
##print(count_smile(':-{(*:-{(:-)8-{O;{(:-{{(X-/$8-{O%:-):-{(:8-{O>{)X-O:8-{O-)X<O:-{('))
###Ответ: 4
##
##
###Тест 5
##print(count_smile('8-{ O:- 8 -{O{(:-{ (:-{ ('))
###Ответ: 0



