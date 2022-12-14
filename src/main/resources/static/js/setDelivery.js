$(document).ready(function () {
    setDate();
});

var length = 0;

function setDate(){
    let select = $(opener.document).find("#setDelivery_send").val();

    $('#route').attr('value', select + "지역배송담당자 배정");

    $.ajax({
        type: 'GET',
        url: `/api/admin/show/${select}`,
        success: function (response) {
            setting(response);
            length = response.length;
        }
    });
}

function setting(response) {
    let deliveryTable = $("#delivery-table-body");
    deliveryTable.empty();

    response.forEach((value, index, array) => {
        let html = "<tr>" +
            `<td><input class="default-text" name="id" name='id' type="text" value="${value['id']}" style="text-align: center" readOnly>`+"</td>"+
            `<td><input class="default-text" name="subRoute" name='subRoute' type="text" value="${value['subRoute']}" style="text-align: center" readOnly>`+"</td>"+
            `<td><input class="default-text" name="zipCode" name='zipCode' type="text" value="${value['zipCode']}" style="text-align: center" readOnly>`+"</td>"+
            `<td><input class="default-text" name="username" name='username' type="text" value="${value['username']}" style="text-align: center">`+"</td></tr>"
        deliveryTable.append(html);
    })
}

function setDone(){
    let ids = [];
    let username = [];

    for(let i=0; i<length; i++){
        ids.push($("input[name=id]").eq(i).val());
        username.push($("input[name=username]").eq(i).val());
    }

    $.ajax({
        type: 'POST',
        url: '/api/admin/update/delivery',
        contentType: 'application/json; charset=utf-8',
        dataType: "text",
        // dataType: "json",
        data: JSON.stringify({
            "ids" : ids,
            "username" : username
        }),
        success: function (response) {
            alert(response);
            opener.document.location.reload();
            self.close();
        },
        error: function (response) {
            /* 에러시 메시지 뽑는 방법 */
            alert(response['responseJSON']['message']);
        }
    })
}