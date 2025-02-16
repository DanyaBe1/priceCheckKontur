document.addEventListener('DOMContentLoaded', function () {
    const barcodeInput = document.getElementById('barcodeInput');
    const resultDiv = document.getElementById('result');
    const updateButton = document.getElementById('updateButton');
    let timeout = null;
    let hideTimeout = null;

    // Автоматический фокус при загрузке
    setTimeout(() => {
        barcodeInput.focus();
    }, 100);

    // Обработчики событий для сканирования
    barcodeInput.addEventListener('input', function() {
        clearTimeout(timeout);
        timeout = setTimeout(processBarcode, 300);
    });

    barcodeInput.addEventListener('keydown', function(event) {
        if (event.key === 'Enter') {
            clearTimeout(timeout);
            processBarcode();
            event.preventDefault();
        }
    });

    // Возврат фокуса при клике
    document.addEventListener('click', function() {
        barcodeInput.focus();
    });

    // Обработчик обновления базы
    updateButton.addEventListener('click', async () => {
        updateButton.disabled = true;
        
        try {
            const response = await fetch('http://localhost:8081/PriceCheckApp_war/updateProducts', {
                method: 'GET'
            });
            
            const result = await response.json();
            
            if (response.status === 200) {
                showStatusMessage(`✅ ${result.message}`, 'success');
            } else if (response.status === 409) {
                showStatusMessage(`⚠️ ${result.message}`, 'warning');
            } else {
                showStatusMessage(`❌ Ошибка ${response.status}: ${result.message}`, 'error');
            }
        } catch (error) {
            if (error instanceof SyntaxError) {
                showStatusMessage('⚠️ Ошибка формата ответа', 'error');
            } else {
                showStatusMessage('⚠️ Ошибка соединения', 'error');
            }
            console.error('Update error:', error);
        } finally {
            updateButton.disabled = false;
        }
    });

    function processBarcode() {
        let barcode = barcodeInput.value.trim();
        barcode = barcode.replace(/[\r\n]+$/, '');

        if (barcode.length >= 8) {
            checkPrice(barcode);
        }
    }

    async function checkPrice(barcode) {
        resultDiv.style.display = 'none';
        resultDiv.innerHTML = '';
        
        resultDiv.textContent = "Идет поиск...";
        resultDiv.style.display = "block";

        try {
            const response = await fetch(`http://localhost:8081/PriceCheckApp_war/getProduct?barcode=${barcode}`);
            const data = await response.json();

            if (data && data.name && data.sellPricePerUnit !== undefined) {
                resultDiv.innerHTML = `
                    <div class="product-name">${data.name}</div>
                    <div class="product-price">${data.sellPricePerUnit} руб.</div>
                `;
            } else {
                resultDiv.textContent = "Товар не найден";
            }
        } catch (error) {
            console.error("Ошибка запроса:", error);
            resultDiv.textContent = "Ошибка. Попробуйте снова";
        }

        barcodeInput.value = "";
        barcodeInput.focus();

        clearTimeout(hideTimeout);
        hideTimeout = setTimeout(() => {
            resultDiv.style.display = "none";
        }, 5000);
    }

    function showStatusMessage(message, type) {
        const existingStatus = document.querySelector('.status-message');
        if (existingStatus) existingStatus.remove();

        const statusDiv = document.createElement('div');
        statusDiv.className = `status-message ${type}`;
        statusDiv.textContent = message;
        
        document.body.appendChild(statusDiv);
        
        setTimeout(() => {
            statusDiv.remove();
        }, 5000);
    }
});