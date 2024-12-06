async function checkPrice() {
    const barcode = document.getElementById('barcodeInput').value.trim();
    const resultDiv = document.getElementById('result');

    if (!barcode) {
        resultDiv.textContent = "Введите штрих-код";
        resultDiv.style.display = "block";
        return;
    }

    resultDiv.textContent = "Идет поиск...";
    resultDiv.style.display = "block";

    try {
        // Выполняем запрос к вашему серверу с полученным штрих-кодом
        const response = await fetch(`http://localhost:8081/PriceCheckApp_war/getProduct?barcode=${barcode}`);
        const data = await response.json();

        // Проверяем, что ответ содержит данные о товаре
        if (data && data.name && data.sellPricePerUnit !== undefined) {
            resultDiv.textContent = `Товар: ${data.name} \nЦена: ${data.sellPricePerUnit} руб.`;
        } else {
            resultDiv.textContent = "Товар не найден";
        }
    } catch (error) {
        console.error("Ошибка запроса:", error);
        resultDiv.textContent = "Ошибка. Попробуйте снова";
    }

    // Очищаем поле ввода
    document.getElementById('barcodeInput').value = "";

    // Скрываем результат через 5 секунд
    setTimeout(() => {
        resultDiv.style.display = "none";
    }, 5000);
}
