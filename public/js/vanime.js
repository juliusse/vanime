window.onload = function () {
    var resultCountDiv = document.getElementById("count");
    var submitButton = document.getElementById("submit");
    var numberInput = document.getElementById("number");
    var wordInput = document.getElementById("minWordLength");
    var digitInput = document.getElementById("maxDigitsInRow");
    var removeSeperatorsInput = document.getElementById("removeSeperators");
    var resultsDiv = document.getElementById("results");

    var results = null;
    var removeSeparators = false;

    var fillResultsList = function () {
        while (resultsDiv.hasChildNodes()) {
            resultsDiv.removeChild(resultsDiv.firstChild)
        }

        if (results != null) {
            resultCountDiv.innerText = results.length;

            for (index in results) {
                if (results.hasOwnProperty(index)) {
                    var text = removeSeparators ? results[index].replace(/-/g, "")
                            : results[index];
                    resultsDiv.appendChild(document.createTextNode(text));
                    resultsDiv.appendChild(document.createElement("br"));
                }
            }
        }
    }

    var processing = function () {

        var number = numberInput.value;
        var wordLength = wordInput.value;
        var digitsInRow = digitInput.value;

        if (number.length == 0 || isNaN(wordLength) || isNaN(digitsInRow)
                || wordLength.length == 0 || digitsInRow.length == 0) {
            results = null;
            fillResultsList();
            resultsDiv.appendChild(document
                    .createTextNode("Please check your input!"));
            resultsDiv.appendChild(document.createElement("br"));

            return false;
        }

        // disable search button
        submitButton.disabled = true;
        numberInput.disabled = true;

        var request = new XMLHttpRequest();

        request.onreadystatechange = function () {
            if (request.readyState == 4) {
                if (request.status == 200) {
                    results = JSON.parse(request.responseText);
                    fillResultsList();

                } else if (request.status >= 400 && request.status < 500) {
                    results = null;
                    fillResultsList();
                    resultsDiv.appendChild(document
                            .createTextNode(request.responseText));
                    resultsDiv.appendChild(document.createElement("br"));
                }

                // enable search button
                submitButton.disabled = false;
                numberInput.disabled = false;
                numberInput.focus();
            }

        }

        request.open("GET", "/api/v1/convert/" + encodeURIComponent(number)
                + "?minWordLength=" + wordLength + "&maxConnectedDigits="
                + digitsInRow, true);
        request.send();

        return false;
    };

    submitButton.onclick = processing;
    numberInput.onkeyup = function (event) {
        if (event.keyCode == 13) {
            processing();
        }
    }

    document.getElementById("removeSeperators").onchange = function () {
        removeSeparators = removeSeperatorsInput.checked;
        fillResultsList();
    };

    numberInput.focus();
}