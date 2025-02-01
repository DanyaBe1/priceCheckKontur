document.addEventListener('DOMContentLoaded', function () {
    const barcodeInput = document.getElementById('barcodeInput');
    const resultDiv = document.getElementById('result');
    let timeout = null; // Таймер для ввода
    let hideTimeout = null; // Таймер скрытия результата

    // Ставим фокус на поле ввода при загрузке
    barcodeInput.focus();

    // Обрабатываем ввод (с задержкой, чтобы избежать лишних запросов)
    barcodeInput.addEventListener('input', function () {
        clearTimeout(timeout);
        timeout = setTimeout(processBarcode, 300); // Задержка 300 мс перед запросом
    });

    // Если сканер отправляет Enter — немедленный запрос
    barcodeInput.addEventListener('keydown', function (event) {
        if (event.key === 'Enter') {
            clearTimeout(timeout);
            processBarcode();
            event.preventDefault();
        }
    });

    function processBarcode() {
        let barcode = barcodeInput.value.trim();
        barcode = barcode.replace(/[\r\n]+$/, ''); // Убираем переносы строк

        if (barcode !== '') {
            checkPrice(barcode);
        }
    }

    async function checkPrice(barcode) {
        resultDiv.textContent = "Идет поиск...";
        resultDiv.style.display = "block";

        try {
            const response = await fetch(`http://localhost:8081/PriceCheckApp_war/getProduct?barcode=${barcode}`);
            const data = await response.json();

            if (data && data.name && data.sellPricePerUnit !== undefined) {
                resultDiv.textContent = `Товар: ${data.name} \nЦена: ${data.sellPricePerUnit} руб.`;
            } else {
                resultDiv.textContent = "Товар не найден";
            }
        } catch (error) {
            console.error("Ошибка запроса:", error);
            resultDiv.textContent = "Ошибка. Попробуйте снова";
        }

        // Очищаем поле ввода и снова ставим фокус
        barcodeInput.value = "";
        barcodeInput.focus();

        // Очищаем старый таймер скрытия и запускаем новый
        clearTimeout(hideTimeout);
        hideTimeout = setTimeout(() => {
            resultDiv.style.display = "none";
        }, 5000);
    }
});
