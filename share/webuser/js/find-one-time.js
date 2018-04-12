/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


$('#btnFindMad').click(function () {
    var phoneNumber = document.getElementById("inputMobile").value;
    var owner = document.getElementById("inputOwner").value;
    var houseNumber = document.getElementById("inputHouseNumber").value;
    var servicePackage = document.getElementById("inputServicePackage").value;
    var setDate = document.getElementById("datetimepicker").value;
    var isCook = document.getElementById('cbCook').checked ? 1 : 0;
    var isKidCare = document.getElementById('cbKidCare').checked ? 1 : 0;
    var isCleanHouse = document.getElementById('cbCleanHouse').checked ? 1 : 0;
    var isOldCare = document.getElementById('cbOldCare').checked ? 1 : 0;
    var note = document.getElementById('txtareNote').value;
    var address = document.getElementById('pac-input').value;
    var longitude = document.getElementById('longitude').value;
    var latitude = document.getElementById('latitude').value;

    document.getElementById("alertSuccess").style.display = "none";
    document.getElementById("alertErrorMsg").style.display = "none";

    if (!address) {
        document.getElementById("alertErrorMsg").style.display = "block";
        document.getElementById('lbErrorMsg').innerHTML = 'Chưa chọn địa chỉ!';
        return;
    }

    if (!phoneNumber) {
        document.getElementById("alertErrorMsg").style.display = "block";
        document.getElementById('lbErrorMsg').innerHTML = 'Chưa nhập số điện thoại!';
        return;
    } else {
        if (phoneNumber.length > 11 || phoneNumber.length < 10) {
            document.getElementById("alertErrorMsg").style.display = "block";
            document.getElementById('lbErrorMsg').innerHTML = 'Số điện thoại không hợp lệ!';
            return;
        }
        var isnum = /^\d+$/.test(phoneNumber);
        if (isnum == false) {
            document.getElementById("alertErrorMsg").style.display = "block";
            document.getElementById('lbErrorMsg').innerHTML = 'Số điện thoại không hợp lệ!';
            return;
        }
    }

    if (!owner) {
        document.getElementById("alertErrorMsg").style.display = "block";
        document.getElementById('lbErrorMsg').innerHTML = 'Chưa nhập tên chủ nhà!';
        return;
    }

    if (!houseNumber) {
        document.getElementById("alertErrorMsg").style.display = "block";
        document.getElementById('lbErrorMsg').innerHTML = 'Chưa nhập số nhà!';
        return;
    }

    if (!servicePackage) {
        document.getElementById("alertErrorMsg").style.display = "block";
        document.getElementById('lbErrorMsg').innerHTML = 'Chưa chọn gói dịch vụ!';
        return;
    }

    if (!setDate) {
        document.getElementById("alertErrorMsg").style.display = "block";
        document.getElementById('lbErrorMsg').innerHTML = 'Chưa chọn ngày làm việc!';
        return;
    } else {
        var hm = setDate.slice(11,16);
        var today = new Date();
        var hours = today.getHours();
        if (hours < 10) {
            hours = "0" + hours;
        }
        var min = today.getMinutes();
        if (min < 10) {
            min = "0" + min;
        }
        var hmToday = hours + ":" + min;
        if (hm < hmToday) {
            document.getElementById("alertErrorMsg").style.display = "block";
            document.getElementById('lbErrorMsg').innerHTML = 'Giờ làm việc không hợp lý!';
            return;
        }
    }

    $.ajax({
        type: 'POST',
        dataType: 'json',
        url: '/check-phone',
        data: {'phoneNumber': phoneNumber},
        cache: false, //fix loop IE
        success: function (data, textStatus, jqXHR) {
            if (data.registered === true) {
                document.getElementById("loginMobile").value = phoneNumber;
                $("#loginModal").modal();
            } else {
                document.getElementById("regisMobile").value = phoneNumber;
                $("#regisModal").modal();
            }
        }
    });
});

$('#btnRegis').click(function () {
    var phoneNumber = document.getElementById("regisMobile").value;
    var owner = document.getElementById("inputOwner").value;
    var password = document.getElementById("regisPassword").value;
    var repassword = document.getElementById("regisRePassword").value;
    var address = document.getElementById('pac-input').value;
    var home = document.getElementById('inputHouseNumber').value;
    var longitude = document.getElementById('longitude').value;
    var latitude = document.getElementById('latitude').value;
    document.getElementById("alertErrorMsgModal").style.display = "none";
    if (!phoneNumber) {
        document.getElementById("alertErrorMsgModal").style.display = "block";
        document.getElementById('lbErrorMsgModal').innerHTML = 'Chưa nhập số điện thoại!';
        return;
    } else {
        if (phoneNumber.length > 11 || phoneNumber.length < 10) {
            document.getElementById("alertErrorMsgModal").style.display = "block";
            document.getElementById('lbErrorMsgModal').innerHTML = 'Số điện thoại không hợp lệ!';
            return;
        }
        var isnum = /^\d+$/.test(phoneNumber);
        if (isnum == false) {
            document.getElementById("alertErrorMsgModal").style.display = "block";
            document.getElementById('lbErrorMsgModal').innerHTML = 'Số điện thoại không hợp lệ!';
            return;
        }
    }

    if (!password) {
        document.getElementById("alertErrorMsgModal").style.display = "block";
        document.getElementById('lbErrorMsgModal').innerHTML = 'Chưa nhập mật khẩu!';
        return;
    } else {
        if (password.length < 6) {
            document.getElementById("alertErrorMsgModal").style.display = "block";
            document.getElementById('lbErrorMsgModal').innerHTML = 'Mật khẩu cần lớn hơn 6 ký tự!';
            return;
        }
    }
    if (!repassword) {
        document.getElementById("alertErrorMsgModal").style.display = "block";
        document.getElementById('lbErrorMsgModal').innerHTML = 'Chưa nhập lại mật khẩu!';
        return;
    } else {
        if (password !== repassword) {
            document.getElementById("alertErrorMsgModal").style.display = "block";
            document.getElementById('lbErrorMsgModal').innerHTML = 'Mật khẩu nhập lại không khớp!';
            return;
        }
    }

    $.ajax({
        type: 'POST',
        dataType: 'json',
        url: '/user/adduser',
        data: {
            'username': owner,
            'mobile': phoneNumber,
            'password': password,
            'usertype': 4,
            'address': address,
            'home': home,
            'longitude': longitude,
            'latitude': latitude,
            'source': 1
        },
        cache: false, //fix loop IE
        success: function (data, textStatus, jqXHR) {
            if (data.response_message === 'Thêm mới thành công') {
                $('#regisModal').modal('hide');
                searchOneTimeMad();
            } else {
                document.getElementById("alertErrorMsg").style.display = "block";
                document.getElementById('lbErrorMsg').innerHTML = 'Đăng ký không thành công!';
                $('#regisModal').modal('hide');
            }
        }
    });

});

$('#btnLogin').click(function () {
    var phoneNumber = document.getElementById("loginMobile").value;
    var password = document.getElementById("loginPassword").value;
    document.getElementById("alertErrorMsgModalLogin").style.display = "none";
    if (!phoneNumber) {
        document.getElementById("alertErrorMsgModalLogin").style.display = "block";
        document.getElementById('lbErrorMsgModalLogin').innerHTML = 'Chưa nhập số điện thoại!';
        return;
    } else {
        if (phoneNumber.length > 11 || phoneNumber.length < 10) {
            document.getElementById("alertErrorMsgModalLogin").style.display = "block";
            document.getElementById('lbErrorMsgModalLogin').innerHTML = 'Số điện thoại không hợp lệ!';
            return;
        }
        var isnum = /^\d+$/.test(phoneNumber);
        if (isnum == false) {
            document.getElementById("alertErrorMsgModalLogin").style.display = "block";
            document.getElementById('lbErrorMsgModalLogin').innerHTML = 'Số điện thoại không hợp lệ!';
            return;
        }
    }

    if (!password) {
        document.getElementById("alertErrorMsgModalLogin").style.display = "block";
        document.getElementById('lbErrorMsgModalLogin').innerHTML = 'Chưa nhập mật khẩu!';
        return;
    } else {
        if (password.length < 6) {
            document.getElementById("alertErrorMsgModalLogin").style.display = "block";
            document.getElementById('lbErrorMsgModalLogin').innerHTML = 'Mật khẩu cần lớn hơn 6 ký tự!';
            return;
        }
    }
    $.ajax({
        type: 'POST',
        dataType: 'json',
        url: '/validate-login',
        data: {
            'phoneNumber': phoneNumber,
            'password': password
        },
        cache: false, //fix loop IE
        success: function (data, textStatus, jqXHR) {
            if (data.login == true) {
                $('#loginModal').modal('hide');
                searchOneTimeMad();
            } else {
                document.getElementById("alertErrorMsgModalLogin").style.display = "block";
                document.getElementById('lbErrorMsgModalLogin').innerHTML = 'Thông tin đăng nhập không chính xác!';
            }
        }
    });

});

searchOneTimeMad = function () {
    var phoneNumber = document.getElementById("inputMobile").value;
    var owner = document.getElementById("inputOwner").value;
    var houseNumber = document.getElementById("inputHouseNumber").value;
    var servicePackage = document.getElementById("inputServicePackage").value;
    var setDate = document.getElementById("datetimepicker").value;
    var isCook = document.getElementById('cbCook').checked ? 1 : 0;
    var isKidCare = document.getElementById('cbKidCare').checked ? 1 : 0;
    var isCleanHouse = document.getElementById('cbCleanHouse').checked ? 1 : 0;
    var isOldCare = document.getElementById('cbOldCare').checked ? 1 : 0;
    var note = document.getElementById('txtareNote').value;
    var address = document.getElementById('pac-input').value;
    var longitude = document.getElementById('longitude').value;
    var latitude = document.getElementById('latitude').value;

    $.ajax({
        type: 'POST',
        dataType: 'json',
        url: '/create-order',
        data: {
            'phoneNumber': phoneNumber,
            'address': address,
            'detail': houseNumber,
            'latitude': latitude,
            'longitude': longitude,
            'cleaning': isCleanHouse,
            'cooking': isCook,
            'babyCare': isKidCare,
            'oldCare': isOldCare,
            'content': note,
            'startDate': setDate,
            'startLength': servicePackage,
            'contactName': owner,
            'contactMobile': phoneNumber,
            'isPeriodical': 0,
            'source': 1
        },
        cache: false, //fix loop IE
        success: function (data, textStatus, jqXHR) {
            document.getElementById("alertSuccess").style.display = "block";
        }
    });
}