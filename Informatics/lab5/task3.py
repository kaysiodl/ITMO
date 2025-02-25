import matplotlib.pyplot as plt
import pandas as pd
import seaborn as sns

df = pd.read_csv("task2.csv", delimiter=";")

df_melted = df.melt(
    id_vars="Дата",
    value_vars=["Открытие", "Максимум", "Минимум", "Закрытие"],
    var_name="Type",
    value_name="Value"
)

plt.figure(figsize=(10, 6))
plt.title("Статистика")
plt.ylabel("Значение")
plt.xlabel("Дата")

sns.boxplot(
    data=df_melted,
    x="Дата", y="Value",
    hue="Type", palette=["blue", "orange", "gray", "yellow"]
)

plt.savefig("task3.png")
plt.show()
