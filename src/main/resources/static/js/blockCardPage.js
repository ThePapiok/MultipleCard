let ok = [false];
let previous = [false];
const buttonId = "blockButton";

window.addEventListener('beforeunload', function (event) {
    if (buttons) {
        buttons = false;
        return;
    }
    event.preventDefault();
    event.returnValue = '';

});
