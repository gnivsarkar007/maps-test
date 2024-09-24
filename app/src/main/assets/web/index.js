window.androidObj = function AndroidClass() {};
//var selectedlist = [];

var svgBody = document.getElementById('div').innerHTML;
var nodex = '.default{fill:#e9e9e9;}';
var democrat = '#00AEF3';
var republican = 'E81B23'; // custom style class has been injected into the SVG body inside HTML
var unknown = '#e9e9e9';
var seed = Math.floor(Math.random() * 10) + 1;
       var colorstyle
                    if(seed % 5 ==0){ colorstyle = unknown} else if(seed % 3 == 0) {colorstyle = republican} else {colorstyle = democrat}

var node = `.selected {fill: ${colorstyle};}`; // custom style class has been injected into the SVG body inside HTML

var svg = document.getElementsByTagName('svg')[0];

var inner = svg.getElementsByTagName('style')[0].innerHTML;
var addingValue = nodex + inner + node;
svg.getElementsByTagName('style')[0].innerHTML = addingValue;

document.addEventListener("click", doSomething);

var svgOutput = document.getElementById("div").outerHTML;

var query = '*[id^=Code_]';
var tablePathList = document.querySelectorAll(query);
var table;
for (table = 0; table < tablePathList.length; table++) {
    tablePathList[table].removeAttribute('style');
    if (tablePathList[table].classList.contains('state')) {
        document.getElementById(tablePathList[table].id).classList.add('selected');
    }
}

function doSomething(e) {
    if (e.target !== e.currentTarget) {
        var clickedItem = e.target.id;
        var itemName;
        var item;

        for (item = 0; item < tablePathList.length; item++) {
            if (clickedItem === tablePathList[item].id) {
                var clickedSvgPath = document.getElementById(clickedItem);
//                clickedSvgPath.classList.toggle("selected");
                itemName = e.target.querySelector('title').innerHTML;
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
        //console.log("Hello " + clickedItem);
        window.androidObj.textToAndroid(itemName);
        document.getElementById('l_value').innerHTML = itemName;
    }
    e.stopPropagation();
}

function updateFromAndroid(message) {
    document.getElementById('l_value').innerHTML = message;
}