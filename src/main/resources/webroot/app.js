function greet() {
    const name = document.getElementById("name").value;
    fetch("/hello?name=" + encodeURIComponent(name))
        .then(response => response.text())
        .then(message => {
            document.getElementById("result").innerHTML = message;
        })
        .catch(err => {
            document.getElementById("result").innerHTML = "Error de red: " + err.message;
        });
}

function getPi() {
    fetch("/pi")
        .then(response => response.text())
        .then(message => {
            document.getElementById("piResult").innerHTML = message;
        })
        .catch(err => {
            document.getElementById("piResult").innerHTML = "Error de red: " + err.message;
        });
}

function getSquare() {
    const value = document.getElementById("value").value;
    fetch("/square?value=" + encodeURIComponent(value))
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => { throw new Error(text); });
            }
            return response.text();
        })
        .then(message => {
            document.getElementById("squareResult").innerHTML = message;
        })
        .catch(err => {
            document.getElementById("squareResult").innerHTML = "Error: " + err.message;
        });
}