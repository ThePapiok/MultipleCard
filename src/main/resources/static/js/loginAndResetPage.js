function addWhiteSpace(e, input, length) {
    let previous = previousVerification;
    if (length === 3) {
        if (previous.charAt(previous.length - 1) === " ") {
            e.value = input.substring(0, length - 1);
        } else {
            e.value += " ";
        }
        input = e.value;
    }
    previousVerification = input;
}

