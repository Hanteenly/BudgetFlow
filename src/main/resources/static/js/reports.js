function loadReports() {
    var year = document.getElementById("year").value;
    var month = document.getElementById("month").value;
    var auth = {
        "Authorization": "Basic " + btoa(
            sessionStorage.getItem("name") + ":" + sessionStorage.getItem("password")
        )
    };

    fetch("/api/reports/monthly?year=" + year + "&month=" + month, { headers: auth })
        .then(function (response) {
            if (response.ok) {
                return response.json();
            }
        })
        .then(function (report) {
            if (report) {
                showMonthly(report);
            }
        });

    fetch("/api/reports/expenses-by-category?year=" + year + "&month=" + month, { headers: auth })
        .then(function (response) {
            if (response.ok) {
                return response.json();
            }
        })
        .then(function (items) {
            if (items && items.length) {
                showByCategory(items);
            } else {
                showByCategoryFromTransactions(year, month, auth);
            }
        });

    fetch("/api/reports/accounts", { headers: auth })
        .then(function (response) {
            if (response.ok) {
                return response.json();
            }
        })
        .then(function (items) {
            if (items) {
                showAccounts(items);
            }
        })
}

function showMonthly(report) {
    var box = document.getElementById("monthly");
    box.textContent = "";
    var tile = document.createElement("div");
    tile.className = "tile";
    var title = document.createElement("strong");
    title.textContent = "Month";
    tile.appendChild(title);
    var line = document.createElement("div");
    line.textContent = "in " + report.income + " · out " + report.expense;
    tile.appendChild(line);
    var meta = document.createElement("div");
    meta.className = "muted";
    meta.textContent = "net " + report.net;
    tile.appendChild(meta);
    box.appendChild(tile);
}

function categoryName(item) {
    if (item.category && item.category.name) {
        return item.category.name;
    }
    if (typeof item.category === "string") {
        return item.category;
    }
    return "Unknown";
}

function showByCategory(items) {
    var box = document.getElementById("by-category");
    box.textContent = "";
    for (var i = 0; i < items.length; i++) {
        var tile = document.createElement("div");
        tile.className = "tile";
        var title = document.createElement("strong");
        title.textContent = categoryName(items[i]);
        tile.appendChild(title);
        var line = document.createElement("div");
        line.textContent = items[i].amount;
        tile.appendChild(line);
        box.appendChild(tile);
    }
}

function monthPrefix(year, month) {
    var m = String(month);
    if (m.length === 1) {
        m = "0" + m;
    }
    return year + "-" + m;
}

function showByCategoryFromTransactions(year, month, auth) {
    fetch("/api/transactions", { headers: auth })
        .then(function (response) {
            if (response.ok) {
                return response.json();
            }
        })
        .then(function (transactions) {
            var totals = {};
            var prefix = monthPrefix(year, month);
            if (!transactions) {
                return;
            }
            for (var i = 0; i < transactions.length; i++) {
                var transaction = transactions[i];
                if (!transaction.category || !transaction.category.name || !transaction.createdAt) {
                    continue;
                }
                if (String(transaction.createdAt).indexOf(prefix) !== 0) {
                    continue;
                }
                var name = transaction.category.name;
                if (!totals[name]) {
                    totals[name] = 0;
                }
                totals[name] += Number(transaction.amount);
            }
            var items = [];
            for (var name in totals) {
                items.push({
                    category: { name: name },
                    amount: totals[name]
                });
            }
            showByCategory(items);
        });
}

function showAccounts(items) {
    var box = document.getElementById("accounts-report");
    box.textContent = "";
    for (var i = 0; i < items.length; i++) {
        var tile = document.createElement("div");
        tile.className = "tile";
        var title = document.createElement("strong");
        title.textContent = items[i].account;
        tile.appendChild(title);
        var line = document.createElement("div");
        line.textContent = items[i].balance;
        tile.appendChild(line);
        box.appendChild(tile);
    }
}

document.getElementById("report-button").addEventListener("click", function (event){
    event.preventDefault();
    loadReports();
})

function logout() {
    sessionStorage.clear();
    window.location.href = "/index.html";
}
document.getElementById("logout-button").addEventListener("click", logout);