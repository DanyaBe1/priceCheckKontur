document.getElementById('barcodeInput').addEventListener('blur', async function(event) {
    const barcodeInput = document.getElementById('barcodeInput');
    let barcode = barcodeInput.value;

    // Убираем символы переноса строки \r и \n, которые могут быть добавлены сканером
    barcode = barcode.replace(/[\r\n]+$/, '').trim();

    // Если строка не пустая, вызываем функцию проверки
    if (barcode !== '') {
        await checkPrice(barcode);
    }
});

async function checkPrice(barcode) {
    const resultDiv = document.getElementById('result');

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
