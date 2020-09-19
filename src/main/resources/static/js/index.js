function checkAccount(account) {
    return /^[a-zA-Z0-9_-]{4,16}$/.test(account);
}

function checkPassword(password) {
    return /^.*(?=.{6,})(?=.*\d)(?=.*[A-Z])(?=.*[a-z])(?=.*[!@#$%^&*? ]).*$/.test(password);
}

function login() {
    showFormMsg('register-msg', 'warning', 'Hello');
    // $('#loginModal').modal('hide');
}

function showFormMsg(parentId, type, msg) {
    const parent = $(`#${parentId}`);
    parent.empty();
    const alertMsg = $(`<div class="alert alert-${type} alert-msg alert-dismissible" role="alert"><button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button><span>${msg}</span></div>`);
    alertMsg.appendTo(parent);
}

function register() {
    const account = $('#register-username').val();
    const password = $('#register-password').val();
    const password1 = $('#register-password1').val();
    if (checkAccount(account)) {
        if (checkPassword(password)) {

        } else {
            showFormMsg('register-msg', 'danger', $('#register-password-tip').val());
            return false;
        }
    } else {
        showFormMsg('register-msg', 'danger', $('#register-username-tip').val());
        return false;
    }
    $('#registerModal').modal('hide');
}

function getCronExample(cronExpression) {
    $.get(`/cron/example?cronExpression=${cronExpression}`, null, (data) => {
        let res;
        if (typeof data === 'string')
            res = JSON.parse(data);
        let content = '';
        if (res.Message !== undefined && res.Message !== null) {
            content = res.Message;
        } else {
            res.data.forEach((item) => {
                content += item + '<br/>';
            })
        }
        $('#cronExampleContent').html(content);
        $('#cronExample').modal('show');
    });
}
