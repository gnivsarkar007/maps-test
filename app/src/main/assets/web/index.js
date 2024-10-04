window.androidObj = function AndroidClass() {};
//var selectedlist = [];
var svgBody = document.getElementById('div').innerHTML;
var node = '.selected {fill: #0C75ED;text-color: white}';
var node1 = '.rep {fill: #FA0000;text-color: white}';// custom style class has been injected into the SVG body inside HTML
var nodex = '.default{fill:#e9e9e9;text-color: black}';
var svg = document.getElementsByTagName('svg')[0];

var inner = svg.getElementsByTagName('style')[0].innerHTML;
var addingValue = nodex + inner + node + node1;
svg.getElementsByTagName('style')[0].innerHTML = addingValue;

document.addEventListener("click", doSomething);
var svgOutput = document.getElementById("div").outerHTML;

var query = '*[id^=Code_]';
var tablePathList = document.querySelectorAll(query);
var table;
for (table = 0; table < tablePathList.length; table++) {
    tablePathList[table].removeAttribute('style');
    if (tablePathList[table].classList.contains('state')) {
        document.getElementById(tablePathList[table].id).classList.add('default');
    }
}
function initialiseMap(dems) {
var query = '*[id^=Code_]';
var tablePathList = document.querySelectorAll(query);
var table;
for (table = 0; table < tablePathList.length; table++) {
console.log(tablePathList[table].id +"==="+ dems["code"])
    tablePathList[table].removeAttribute('style');
    if (tablePathList[table].classList.contains('state')) {
    if(tablePathList[table].id == dems["code"]) {
        document.getElementById(tablePathList[table].id).classList.add('selected');
    } else if(tablePathList[table].id == dems["code"]) {
        document.getElementById(tablePathList[table].id).classList.add('rep');
    } else {
        document.getElementById(tablePathList[table].id).classList.add('default');

    }
    }
}
}

function doSomething(e) {
    if (e.target !== e.currentTarget) {
        var clickedItem = e.target.id;
        var itemName;
        var item;
        var regex = /\bka\b/i;
        for (item = 0; item < tablePathList.length; item++) {
            if (clickedItem === tablePathList[item].id) {
                var clickedSvgPath = document.getElementById(clickedItem);

                itemName = e.target.querySelector('title').innerHTML;
//                if(e.target.querySelector('title').innerHTML.search('ka')) {
//                    clickedSvgPath.classList.toggle("rep");
//                } else {
//                clickedSvgPath.classList.toggle("selected");
//                }
                /*if (!selectedlist.includes(clickedItem)) {
                    itemName = e.target.querySelector('title').innerHTML;
                    selectedlist.push(clickedItem);
                } else {
                    var index = selectedlist.indexOf(clickedItem);
                    if (index > -1) {
                        selectedlist.splice(index, 1);
                    }
                }*/
            }
        }
        console.log("Hello " + itemName);
        window.androidObj.textToAndroid(itemName);
        document.getElementById('l_value').innerHTML = itemName;
    }
    e.stopPropagation();
}

function updateFromAndroid(message) {
    document.getElementById('l_value').innerHTML = message;
}