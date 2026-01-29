function clearInput() {
    const input = document.getElementById('messageInput');
    if (input) input.value = '';
}

function loadSample() {
    const sampleMessage = `{1:F01BANKDEFFXXXX0000000000}{2:I940BANKDEFFXXXXN}{3:{108:TEST123456789}}{4:\n:20:REFERENCE123\n:25:12345678\n:28C:12345,00\n:60F:C160101EUR1234,56\n:61F:160101EUR1234,56\n:86:Account Details\n:62F:C160101EUR2469,12\n:64:C160101EUR1234,56\n-}`;
    const input = document.getElementById('messageInput');
    if (input) input.value = sampleMessage;
}

function resetForm() {
    const form = document.querySelector('.composer-form') || document.querySelector('.validator-form');
    if (form) form.reset();
}

function copyMessage() {
    const messageOutput = document.querySelector('.message-output');
    if (!messageOutput) return;

    const text = messageOutput.textContent;
    navigator.clipboard.writeText(text).then(() => {
        showToast('Message copied to clipboard!');
    }).catch(err => {
        console.error('Failed to copy: ', err);
    });
}

function downloadMessage() {
    const messageOutput = document.querySelector('.message-output');
    if (!messageOutput) return;

    const text = messageOutput.textContent;
    const blob = new Blob([text], { type: 'text/plain' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'swift-message.txt';
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    window.URL.revokeObjectURL(url);
}

function showToast(message) {
    // Simple alert for now, can be improved with a nice Toast UI
    alert(message);
}
