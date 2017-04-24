/**
 * Created by gao_yn on 2017/1/17.
 */
function init(){

}
$(function(){
    var map = new AMap.Map('container', {
        center: [116.480983, 39.989628],
        zoom: 10
    });
    var _onClick = function(position){
        map.setZoomAndCenter(18, position);

    };
    var lnglats=[
        [116.368904,39.923423],
        [116.382122,39.921176],
        [116.387271,39.922501],
        [116.398258,39.914600]
    ];
    for(var i=0;i<5;i++){
        var marker;
        var content= '<div class="perPos">' +
            '<img src="page/web/css/img/'+(i+1)+'.jpg"></div>';
        marker = new AMap.Marker({
            content: content,
            position: lnglats[i],
            offset: new AMap.Pixel(0,0),
            map: map
        });
        var t=[116.480983+i, 39.989628];
        marker.index=i;
        marker.t=lnglats[i];
        marker.setMap(map);
        AMap.event.addListener(marker,'dblclick',function(e){
            _onClick(e.target.t);
            $('.perPos').removeClass('active');
            $('.perPos').eq(e.target.index).addClass('active');
        });
    }
    map.setFitView();
});
