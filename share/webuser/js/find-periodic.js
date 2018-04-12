/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


function handleClickCustom(cb) {
    if (cb.checked) {
        document.getElementById("div-setting-df").style.display = "none";
        var cb2 = document.getElementById("cb2").checked;
        if (cb2) {
            document.getElementById("div-setting-2").style.display = "block";
        } else {
            document.getElementById("div-setting-2").style.display = "none";
        }
        var cb3 = document.getElementById("cb3").checked;
        if (cb3) {
            document.getElementById("div-setting-3").style.display = "block";
        } else {
            document.getElementById("div-setting-3").style.display = "none";
        }
        var cb4 = document.getElementById("cb4").checked;
        if (cb4) {
            document.getElementById("div-setting-4").style.display = "block";
        } else {
            document.getElementById("div-setting-4").style.display = "none";
        }
        var cb5 = document.getElementById("cb5").checked;
        if (cb5) {
            document.getElementById("div-setting-5").style.display = "block";
        } else {
            document.getElementById("div-setting-5").style.display = "none";
        }
        var cb6 = document.getElementById("cb6").checked;
        if (cb6) {
            document.getElementById("div-setting-6").style.display = "block";
        } else {
            document.getElementById("div-setting-6").style.display = "none";
        }
        var cb7 = document.getElementById("cb7").checked;
        if (cb7) {
            document.getElementById("div-setting-7").style.display = "block";
        } else {
            document.getElementById("div-setting-7").style.display = "none";
        }
        var cbcn = document.getElementById("cbcn").checked;
        if (cbcn) {
            document.getElementById("div-setting-cn").style.display = "block";
        } else {
            document.getElementById("div-setting-cn").style.display = "none";
        }

    } else {
        document.getElementById("div-setting-df").style.display = "block";
        document.getElementById("div-setting-2").style.display = "none";
        document.getElementById("div-setting-3").style.display = "none";
        document.getElementById("div-setting-4").style.display = "none";
        document.getElementById("div-setting-5").style.display = "none";
        document.getElementById("div-setting-6").style.display = "none";
        document.getElementById("div-setting-7").style.display = "none";
        document.getElementById("div-setting-cn").style.display = "none";
    }
}

function handleClick2(cb) {
    var cbcustom = document.getElementById("refrigerator-clean").checked;
    if (cbcustom) {
        if (cb.checked) {
            document.getElementById("div-setting-2").style.display = "block";
        } else {
            document.getElementById("div-setting-2").style.display = "none";
        }
    }
}
function handleClick3(cb) {
    var cbcustom = document.getElementById("refrigerator-clean").checked;
    if (cbcustom) {
        if (cb.checked) {
            document.getElementById("div-setting-3").style.display = "block";
        } else {
            document.getElementById("div-setting-3").style.display = "none";
        }
    }
}
function handleClick4(cb) {
    var cbcustom = document.getElementById("refrigerator-clean").checked;
    if (cbcustom) {
        if (cb.checked) {
            document.getElementById("div-setting-4").style.display = "block";
        } else {
            document.getElementById("div-setting-4").style.display = "none";
        }
    }
}
function handleClick5(cb) {
    var cbcustom = document.getElementById("refrigerator-clean").checked;
    if (cbcustom) {
        if (cb.checked) {
            document.getElementById("div-setting-5").style.display = "block";
        } else {
            document.getElementById("div-setting-5").style.display = "none";
        }
    }
}
function handleClick6(cb) {
    var cbcustom = document.getElementById("refrigerator-clean").checked;
    if (cbcustom) {
        if (cb.checked) {
            document.getElementById("div-setting-6").style.display = "block";
        } else {
            document.getElementById("div-setting-6").style.display = "none";
        }
    }
}
function handleClick7(cb) {
    var cbcustom = document.getElementById("refrigerator-clean").checked;
    if (cbcustom) {
        if (cb.checked) {
            document.getElementById("div-setting-7").style.display = "block";
        } else {
            document.getElementById("div-setting-7").style.display = "none";
        }
    }
}
function handleClickcn(cb) {
    var cbcustom = document.getElementById("refrigerator-clean").checked;
    if (cbcustom) {
        if (cb.checked) {
            document.getElementById("div-setting-cn").style.display = "block";
        } else {
            document.getElementById("div-setting-cn").style.display = "none";
        }
    }
}



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

    var is2 = document.getElementById('cb2').checked;
    var is3 = document.getElementById('cb3').checked;
    var is4 = document.getElementById('cb4').checked;
    var is5 = document.getElementById('cb5').checked;
    var is6 = document.getElementById('cb6').checked;
    var is7 = document.getElementById('cb7').checked;
    var iscn = document.getElementById('cbcn').checked;
    var iscustom = document.getElementById('refrigerator-clean').checked;

    var servicePackageDf = document.getElementById('inputServicePackagedf').value;
    var servicePackage2 = document.getElementById('inputServicePackage2').value;
    var servicePackage3 = document.getElementById('inputServicePackage3').value;
    var servicePackage4 = document.getElementById('inputServicePackage4').value;
    var servicePackage5 = document.getElementById('inputServicePackage5').value;
    var servicePackage6 = document.getElementById('inputServicePackage6').value;
    var servicePackage7 = document.getElementById('inputServicePackage7').value;
    var servicePackagecn = document.getElementById('inputServicePackagecn').value;

    var timeStartDf = document.getElementById('timpickerdf').value;
    var timeStart2 = document.getElementById('timpicker2').value;
    var timeStart3 = document.getElementById('timpicker3').value;
    var timeStart4 = document.getElementById('timpicker4').value;
    var timeStart5 = document.getElementById('timpicker5').value;
    var timeStart6 = document.getElementById('timpicker6').value;
    var timeStart7 = document.getElementById('timpicker7').value;
    var timeStartcn = document.getElementById('timpickercn').value;

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
    }else {
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

    if (!iscustom && (!servicePackageDf || !timeStartDf)) {
        document.getElementById("alertErrorMsg").style.display = "block";
        document.getElementById('lbErrorMsg').innerHTML = 'Chưa nhập thông tin gói mặc định!';
        return;
    }

    if (iscustom) {
        if (is2 && (!servicePackage2 || !timeStart2)) {
            document.getElementById("alertErrorMsg").style.display = "block";
            document.getElementById('lbErrorMsg').innerHTML = 'Chưa nhập thông tin gói thứ 2!';
            return;
        }
        if (is3 && (!servicePackage3 || !timeStart3)) {
            document.getElementById("alertErrorMsg").style.display = "block";
            document.getElementById('lbErrorMsg').innerHTML = 'Chưa nhập thông tin gói thứ 3!';
            return;
        }
        if (is4 && (!servicePackage4 || !timeStart4)) {
            document.getElementById("alertErrorMsg").style.display = "block";
            document.getElementById('lbErrorMsg').innerHTML = 'Chưa nhập thông tin gói thứ 4!';
            return;
        }
        if (is5 && (!servicePackage5 || !timeStart5)) {
            document.getElementById("alertErrorMsg").style.display = "block";
            document.getElementById('lbErrorMsg').innerHTML = 'Chưa nhập thông tin gói thứ 5!';
            return;
        }
        if (is6 && (!servicePackage6 || !timeStart6)) {
            document.getElementById("alertErrorMsg").style.display = "block";
            document.getElementById('lbErrorMsg').innerHTML = 'Chưa nhập thông tin gói thứ 6!';
            return;
        }
        if (is7 && (!servicePackage7 || !timeStart7)) {
            document.getElementById("alertErrorMsg").style.display = "block";
            document.getElementById('lbErrorMsg').innerHTML = 'Chưa nhập thông tin gói thứ 7!';
            return;
        }
        if (iscn && (!servicePackagecn || !timeStartcn)) {
            document.getElementById("alertErrorMsg").style.display = "block";
            document.getElementById('lbErrorMsg').innerHTML = 'Chưa nhập thông tin gói chủ nhật!';
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
            'home':home,
            'longitude':longitude,
            'latitude':latitude,
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
    var startDate = document.getElementById("datetimepicker").value;
    var isCook = document.getElementById('cbCook').checked ? 1 : 0;
    var isKidCare = document.getElementById('cbKidCare').checked ? 1 : 0;
    var isCleanHouse = document.getElementById('cbCleanHouse').checked ? 1 : 0;
    var isOldCare = document.getElementById('cbOldCare').checked ? 1 : 0;
    var note = document.getElementById('txtareNote').value;
    var address = document.getElementById('pac-input').value;
    var longitude = document.getElementById('longitude').value;
    var latitude = document.getElementById('latitude').value;

    var is2 = document.getElementById('cb2').checked;
    var is3 = document.getElementById('cb3').checked;
    var is4 = document.getElementById('cb4').checked;
    var is5 = document.getElementById('cb5').checked;
    var is6 = document.getElementById('cb6').checked;
    var is7 = document.getElementById('cb7').checked;
    var iscn = document.getElementById('cbcn').checked;
    var iscustom = document.getElementById('refrigerator-clean').checked;

    var servicePackageDf = document.getElementById('inputServicePackagedf').value;
    var servicePackage2 = document.getElementById('inputServicePackage2').value;
    var servicePackage3 = document.getElementById('inputServicePackage3').value;
    var servicePackage4 = document.getElementById('inputServicePackage4').value;
    var servicePackage5 = document.getElementById('inputServicePackage5').value;
    var servicePackage6 = document.getElementById('inputServicePackage6').value;
    var servicePackage7 = document.getElementById('inputServicePackage7').value;
    var servicePackagecn = document.getElementById('inputServicePackagecn').value;

    var timeStartDf = document.getElementById('timpickerdf').value;
    var timeStart2 = document.getElementById('timpicker2').value;
    var timeStart3 = document.getElementById('timpicker3').value;
    var timeStart4 = document.getElementById('timpicker4').value;
    var timeStart5 = document.getElementById('timpicker5').value;
    var timeStart6 = document.getElementById('timpicker6').value;
    var timeStart7 = document.getElementById('timpicker7').value;
    var timeStartcn = document.getElementById('timpickercn').value;

    var monStart = '';
    var monLength = '';
    if (is2 && !iscustom && servicePackageDf && timeStartDf) {
        monStart = timeStartDf;
        monLength = servicePackageDf;
    }
    if (is2 && iscustom && servicePackage2 && timeStart2) {
        monStart = timeStart2;
        monLength = servicePackage2;
    }

    var tueStart = '';
    var tueLength = '';
    if (is3 && !iscustom && servicePackageDf && timeStartDf) {
        tueStart = timeStartDf;
        tueLength = servicePackageDf;
    }
    if (is3 && iscustom && servicePackage3 && timeStart3) {
        tueStart = timeStart3;
        tueLength = servicePackage3;
    }

    var wedStart = '';
    var wedLength = '';
    if (is4 && !iscustom && servicePackageDf && timeStartDf) {
        wedStart = timeStartDf;
        wedLength = servicePackageDf;
    }
    if (is4 && iscustom && servicePackage4 && timeStart4) {
        wedStart = timeStart4;
        wedLength = servicePackage4;
    }

    var thuStart = '';
    var thuLength = '';
    if (is5 && !iscustom && servicePackageDf && timeStartDf) {
        thuStart = timeStartDf;
        thuLength = servicePackageDf;
    }
    if (is5 && iscustom && servicePackage5 && timeStart5) {
        thuStart = timeStart5;
        thuLength = servicePackage5;
    }

    var friStart = '';
    var friLength = '';
    if (is6 && !iscustom && servicePackageDf && timeStartDf) {
        friStart = timeStartDf;
        friLength = servicePackageDf;
    }
    if (is6 && iscustom && servicePackage6 && timeStart6) {
        friStart = timeStart6;
        friLength = servicePackage6;
    }

    var satStart = '';
    var satLength = '';
    if (is7 && !iscustom && servicePackageDf && timeStartDf) {
        satStart = timeStartDf;
        satLength = servicePackageDf;
    }
    if (is7 && iscustom && servicePackage7 && timeStart7) {
        satStart = timeStart7;
        satLength = servicePackage7;
    }

    var sunStart = '';
    var sunLength = '';
    if (iscn && !iscustom && servicePackageDf && timeStartDf) {
        sunStart = timeStartDf;
        sunLength = servicePackageDf;
    }
    if (iscn && iscustom && servicePackagecn && timeStartcn) {
        sunStart = timeStartcn;
        sunLength = servicePackagecn;
    }


    $.ajax({
        type: 'POST',
        dataType: 'json',
        url: '/create-order',
        data: {
            'phoneNumber': phoneNumber,
            'address': address,
            'detail':houseNumber,
            'latitude': latitude,
            'longitude': longitude,
            'cleaning': isCleanHouse,
            'cooking': isCook,
            'babyCare': isKidCare,
            'oldCare': isOldCare,
            'content': note,
            'startDate': startDate,
            'startLength': servicePackage,
            'contactName': owner,
            'contactMobile': phoneNumber,
            'isPeriodical': 1,
            'expireTime': servicePackage,
            'monStart': monStart,
            'monLength': monLength,
            'tueStart': tueStart,
            'tueLength': tueLength,
            'wedStart': wedStart,
            'wedLength': wedLength,
            'thuStart': thuStart,
            'thuLength': thuLength,
            'friStart': friStart,
            'friLength': friLength,
            'satStart': satStart,
            'satLength': satLength,
            'sunStart': sunStart,
            'sunLength': sunLength,
            'source':1
        },
        cache: false, //fix loop IE
        success: function (data, textStatus, jqXHR) {
            document.getElementById("alertSuccess").style.display = "block";
        }
    });
}