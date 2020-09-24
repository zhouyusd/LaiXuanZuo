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

function checkTaskInfo(task, flag) {
    const ele = flag === 1 ? 'add-task-msg' : 'edit-task-msg';
    if (task.wechatSessionID === undefined || task.wechatSessionID === '') {
        showFormMsg(ele, 'danger', $('#invalidSessionID').val());
        return false;
    }
    if (task.position === undefined || task.position.split(",").length % 3 !== 0) {
        showFormMsg(ele, 'danger', $('#invalidPosition').val());
        return false;
    }
    if (task.status !== "true" && task.status !== "false") {
        showFormMsg(ele, 'danger', $('#invalidStatus').val());
        return false;
    }
    if (task.remarks === '-1') {
        showFormMsg(ele, 'danger', $('#invalidRemarks').val());
        return false;
    }
    return true;
}

function editTask() {
    const taskInfo = {
        id: parseInt($('#edit-task-id').text()),
        wechatSessionID: $('#edit-task-wechat-session-id').val(),
        position: $('#edit-task-position').val(),
        status: $("[name='task-status1']").val() || true,
        remarks: $('#edit-task-remarks').text()
    };
    if (checkTaskInfo(taskInfo, 2)) {
        $.ajax({
            url: "/tasks",
            data: JSON.stringify(taskInfo),
            type: "PUT",
            dataType: "json",
            contentType: "application/json",
            success: function (data) {
                if (data.status !== 20000) {
                    showFormMsg('edit-task-msg', 'danger', $('#editFailed').val());
                    return false;
                }
                let taskInfo1 = taskInfo;
                taskInfo1.id += 1;
                $.ajax({
                    url: "/tasks",
                    data: JSON.stringify(taskInfo1),
                    type: "PUT",
                    dataType: "json",
                    contentType: "application/json",
                    success: function (data) {
                        $('#editTaskModal').modal('hide');
                        location.reload();
                    }
                });
            }
        });
        return true;
    } else {
        return false;
    }
}

function newTask() {
    const taskInfo = {
        beanName: 'xuanzuoTask',
        methodName: 'reserve',
        wechatSessionID: $('#task-wechat-session-id').val(),
        position: $('#task-position').val(),
        cronExpression: '00 20 19 1/1 * ?',
        status: $("[name='task-status']").val(),
        remarks: $('#task-remarks').val()
    };
    if (checkTaskInfo(taskInfo, 1)) {
        $.ajax({
            url: "/tasks",
            data: JSON.stringify(taskInfo),
            type: "POST",
            dataType: "json",
            contentType: "application/json",
            success: function (data) {
                if (data.status !== 20000) {
                    showFormMsg('add-task-msg', 'danger', $('#addFailed').val());
                    return false;
                }
                let taskInfo1 = taskInfo;
                taskInfo1.cronExpression = '0 0/30 * * * ? ';
                taskInfo1.methodName = 'keepAlive';
                $.ajax({
                    url: "/tasks",
                    data: JSON.stringify(taskInfo1),
                    type: "POST",
                    dataType: "json",
                    contentType: "application/json",
                    success: function (data) {
                        $('#addTaskModal').modal('hide');
                        location.reload();
                    }
                });
            }
        });
        return true;
    } else {
        return false;
    }
}

function preShow() {
    $('#edit-task-id').text('undefined');
    $('#edit-task-remarks').text('undefined');
    $('#edit-task-position').val('');
    const st = $('#edit-status-id');
    st.empty();
    $('<label class="radio-inline"><input type="radio" checked name="task-status1" id="task-status-3" value="true">开启</label><label class="radio-inline"><input type="radio" name="task-status1" id="task-status-4" value="false">关闭</label>').appendTo(st);
}

function showEditTaskModal(task) {
    // console.log(task)
    preShow();
    $('#edit-task-id').text(task.id);
    $('#edit-task-remarks').text(task.remarks);
    $('#edit-task-wechat-session-id').val(task.wechatSessionID);
    $('#edit-task-position').val(task.position);
    const editRadio = $("[name='task-status1']");
    if (task.status) {
        // console.log($("[name='task-status1']"))
        editRadio.eq(0).attr("checked", true);
        editRadio.eq(1).attr("checked", false);
    } else {
        editRadio.eq(0).attr("checked", false);
        editRadio.eq(1).attr("checked", true);
    }
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
