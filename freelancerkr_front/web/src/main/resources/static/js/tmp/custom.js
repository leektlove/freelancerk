$.extend(true, $.fn.countTo.defaults, {
    // from: 0,               // the number the element should start at
    // to: 0,                 // the number the element should end at
    speed: 2500,           // how long it should take to count between the target numbers
    // refreshInterval: 100,  // how often the element should be updated
    decimals: 0,           // the number of decimal places to show
    formatter: formatterToComma,  // handler for formatting the value before rendering
    onUpdate: null,        // callback method for every time the element is updated
    onComplete: null       // callback method for when the element finishes updating
});

function formatterToComma(value, settings) {
    console.log(value.toLocaleString());
    var val = value.toLocaleString();
    return value.toLocaleString().split(".")[0];
}