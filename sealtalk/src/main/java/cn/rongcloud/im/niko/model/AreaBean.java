package cn.rongcloud.im.niko.model;

import java.io.Serializable;
import java.util.List;

public class AreaBean {


    /**
     * province : [{"province_code":"108294","city":[{"city_code":"118137","city_name":"长春"},{"city_code":"118139","city_name":"四平"},{"city_code":"118140","city_name":"辽源"},{"city_code":"118141","city_name":"通化"},{"city_code":"118142","city_name":"白山"},{"city_code":"118143","city_name":"松原"},{"city_code":"118144","city_name":"白城"},{"city_code":"118145","city_name":"延边"},{"city_code":"129059","city_name":"吉林"}],"province_name":"吉林"},{"province_code":"108306","city":[{"city_code":"118290","city_name":"肇庆"},{"city_code":"118286","city_name":"佛山"},{"city_code":"118297","city_name":"东莞"},{"city_code":"118281","city_name":"广州"},{"city_code":"118292","city_name":"梅州"},{"city_code":"118288","city_name":"湛江"},{"city_code":"118299","city_name":"潮州"},{"city_code":"118283","city_name":"珠海"},{"city_code":"118294","city_name":"河源"},{"city_code":"118301","city_name":"云浮"},{"city_code":"118285","city_name":"韶关"},{"city_code":"118296","city_name":"清远"},{"city_code":"118291","city_name":"惠州"},{"city_code":"118287","city_name":"江门"},{"city_code":"118298","city_name":"中山"},{"city_code":"118282","city_name":"深圳"},{"city_code":"118293","city_name":"汕尾"},{"city_code":"118289","city_name":"茂名"},{"city_code":"118300","city_name":"揭阳"},{"city_code":"118284","city_name":"汕头"},{"city_code":"118295","city_name":"阳江"}],"province_name":"广东"},{"province_code":"108317","city":[{"city_code":"118407","city_name":"中卫"},{"city_code":"118404","city_name":"石嘴山"},{"city_code":"118405","city_name":"吴忠"},{"city_code":"118406","city_name":"固原"},{"city_code":"118403","city_name":"银川"}],"province_name":"宁夏"},{"province_code":"108301","city":[{"city_code":"118209","city_name":"南昌"},{"city_code":"118210","city_name":"景德镇"},{"city_code":"118211","city_name":"萍乡"},{"city_code":"118212","city_name":"九江"},{"city_code":"118213","city_name":"新余"},{"city_code":"118214","city_name":"鹰潭"},{"city_code":"118215","city_name":"赣州"},{"city_code":"118216","city_name":"吉安"},{"city_code":"118217","city_name":"宜春"},{"city_code":"118218","city_name":"抚州"},{"city_code":"118219","city_name":"上饶"}],"province_name":"江西"},{"province_code":"108312","city":[{"city_code":"118350","city_name":"玉溪"},{"city_code":"118362","city_name":"怒江"},{"city_code":"130880","city_name":"普洱"},{"city_code":"118349","city_name":"曲靖"},{"city_code":"118355","city_name":"临沧"},{"city_code":"118361","city_name":"德宏"},{"city_code":"118348","city_name":"昆明"},{"city_code":"118360","city_name":"大理"},{"city_code":"118359","city_name":"楚雄"},{"city_code":"118353","city_name":"丽江"},{"city_code":"118358","city_name":"西双版纳"},{"city_code":"118352","city_name":"昭通"},{"city_code":"118357","city_name":"红河"},{"city_code":"118351","city_name":"保山"},{"city_code":"118363","city_name":"迪庆"},{"city_code":"118356","city_name":"文山"}],"province_name":"云南"},{"province_code":"108296","city":[],"province_name":"上海"},{"province_code":"108308","city":[{"city_code":"118316","city_name":"海口"},{"city_code":"118317","city_name":"三亚"},{"city_code":"131407","city_name":"琼海"},{"city_code":"131433","city_name":"儋州"},{"city_code":"131492","city_name":"保亭"},{"city_code":"131497","city_name":"文昌"},{"city_code":"131533","city_name":"三沙"}],"province_name":"海南"},{"province_code":"108291","city":[{"city_code":"118099","city_name":"太原"},{"city_code":"118100","city_name":"大同"},{"city_code":"118102","city_name":"阳泉"},{"city_code":"118103","city_name":"长治"},{"city_code":"118104","city_name":"晋城"},{"city_code":"118105","city_name":"朔州"},{"city_code":"118106","city_name":"晋中"},{"city_code":"118107","city_name":"运城"},{"city_code":"118108","city_name":"忻州"},{"city_code":"118109","city_name":"临汾"},{"city_code":"118110","city_name":"吕梁"}],"province_name":"山西"},{"province_code":"108303","city":[{"city_code":"118243","city_name":"新乡"},{"city_code":"118248","city_name":"三门峡"},{"city_code":"118242","city_name":"鹤壁"},{"city_code":"118247","city_name":"漯河"},{"city_code":"118241","city_name":"焦作"},{"city_code":"118253","city_name":"驻马店"},{"city_code":"118246","city_name":"许昌"},{"city_code":"118240","city_name":"平顶山"},{"city_code":"118252","city_name":"周口"},{"city_code":"128517","city_name":"济源"},{"city_code":"118239","city_name":"洛阳"},{"city_code":"118245","city_name":"濮阳"},{"city_code":"118251","city_name":"信阳"},{"city_code":"118238","city_name":"开封"},{"city_code":"118244","city_name":"安阳"},{"city_code":"118250","city_name":"商丘"},{"city_code":"118249","city_name":"南阳"},{"city_code":"118237","city_name":"郑州"}],"province_name":"河南"},{"province_code":"108314","city":[{"city_code":"118371","city_name":"西安"},{"city_code":"118372","city_name":"铜川"},{"city_code":"118373","city_name":"宝鸡"},{"city_code":"118374","city_name":"咸阳"},{"city_code":"118375","city_name":"渭南"},{"city_code":"118376","city_name":"延安"},{"city_code":"118377","city_name":"汉中"},{"city_code":"118378","city_name":"榆林"},{"city_code":"118379","city_name":"安康"},{"city_code":"118380","city_name":"商洛"}],"province_name":"陕西"},{"province_code":"108298","city":[{"city_code":"118172","city_name":"杭州"},{"city_code":"118173","city_name":"宁波"},{"city_code":"118174","city_name":"温州"},{"city_code":"118175","city_name":"嘉兴"},{"city_code":"118176","city_name":"湖州"},{"city_code":"118177","city_name":"绍兴"},{"city_code":"118178","city_name":"金华"},{"city_code":"118179","city_name":"衢州"},{"city_code":"118180","city_name":"舟山"},{"city_code":"118181","city_name":"台州"},{"city_code":"118182","city_name":"丽水"}],"province_name":"浙江"},{"province_code":"108293","city":[{"city_code":"118130","city_name":"营口"},{"city_code":"118129","city_name":"锦州"},{"city_code":"118123","city_name":"沈阳"},{"city_code":"118135","city_name":"朝阳"},{"city_code":"118128","city_name":"丹东"},{"city_code":"118134","city_name":"铁岭"},{"city_code":"118127","city_name":"本溪"},{"city_code":"118133","city_name":"盘锦"},{"city_code":"118126","city_name":"抚顺"},{"city_code":"118132","city_name":"辽阳"},{"city_code":"118125","city_name":"鞍山"},{"city_code":"118131","city_name":"阜新"},{"city_code":"118136","city_name":"葫芦岛"},{"city_code":"118124","city_name":"大连"}],"province_name":"辽宁"},{"province_code":"108305","city":[{"city_code":"118278","city_name":"怀化"},{"city_code":"118272","city_name":"岳阳"},{"city_code":"118277","city_name":"永州"},{"city_code":"118271","city_name":"邵阳"},{"city_code":"118276","city_name":"郴州"},{"city_code":"118270","city_name":"衡阳"},{"city_code":"118269","city_name":"湘潭"},{"city_code":"118275","city_name":"益阳"},{"city_code":"118268","city_name":"株洲"},{"city_code":"118274","city_name":"张家界"},{"city_code":"118280","city_name":"湘西"},{"city_code":"118279","city_name":"娄底"},{"city_code":"118267","city_name":"长沙"},{"city_code":"118273","city_name":"常德"}],"province_name":"湖南"},{"province_code":"108289","city":[],"province_name":"天津"},{"province_code":"108316","city":[{"city_code":"118395","city_name":"西宁"},{"city_code":"118396","city_name":"海东"},{"city_code":"118397","city_name":"海北"},{"city_code":"118398","city_name":"黄南"},{"city_code":"118399","city_name":"海南"},{"city_code":"118400","city_name":"果洛"},{"city_code":"118401","city_name":"玉树"},{"city_code":"118402","city_name":"海西"}],"province_name":"青海"},{"province_code":"108300","city":[{"city_code":"118200","city_name":"福州"},{"city_code":"118201","city_name":"厦门"},{"city_code":"118202","city_name":"莆田"},{"city_code":"118203","city_name":"三明"},{"city_code":"118204","city_name":"泉州"},{"city_code":"118205","city_name":"漳州"},{"city_code":"118206","city_name":"南平"},{"city_code":"118207","city_name":"龙岩"},{"city_code":"118208","city_name":"宁德"}],"province_name":"福建"},{"province_code":"108311","city":[{"city_code":"118339","city_name":"贵阳"},{"city_code":"118340","city_name":"六盘水"},{"city_code":"118341","city_name":"遵义"},{"city_code":"118342","city_name":"安顺"},{"city_code":"118343","city_name":"铜仁"},{"city_code":"118344","city_name":"毕节"},{"city_code":"118345","city_name":"黔西南"},{"city_code":"118346","city_name":"黔东南"},{"city_code":"118347","city_name":"黔南"}],"province_name":"贵州"},{"province_code":"108295","city":[{"city_code":"118147","city_name":"齐齐哈尔"},{"city_code":"118153","city_name":"牡丹江"},{"city_code":"118158","city_name":"大兴安岭"},{"city_code":"118146","city_name":"哈尔滨"},{"city_code":"118152","city_name":"伊春"},{"city_code":"118157","city_name":"绥化"},{"city_code":"118151","city_name":"大庆"},{"city_code":"118156","city_name":"黑河"},{"city_code":"118150","city_name":"双鸭山"},{"city_code":"118149","city_name":"鹤岗"},{"city_code":"118155","city_name":"七台河"},{"city_code":"118148","city_name":"鸡西"},{"city_code":"118154","city_name":"佳木斯"}],"province_name":"黑龙江"},{"province_code":"108307","city":[{"city_code":"118315","city_name":"崇左"},{"city_code":"118308","city_name":"钦州"},{"city_code":"118302","city_name":"南宁"},{"city_code":"118314","city_name":"来宾"},{"city_code":"118307","city_name":"防城港"},{"city_code":"118313","city_name":"河池"},{"city_code":"118306","city_name":"北海"},{"city_code":"118312","city_name":"贺州"},{"city_code":"118305","city_name":"梧州"},{"city_code":"118311","city_name":"百色"},{"city_code":"118304","city_name":"桂林"},{"city_code":"118310","city_name":"玉林"},{"city_code":"118309","city_name":"贵港"},{"city_code":"118303","city_name":"柳州"}],"province_name":"广西"},{"province_code":"108290","city":[{"city_code":"118088","city_name":"石家庄"},{"city_code":"118089","city_name":"唐山"},{"city_code":"118090","city_name":"秦皇岛"},{"city_code":"118091","city_name":"邯郸"},{"city_code":"118092","city_name":"邢台"},{"city_code":"118093","city_name":"保定"},{"city_code":"118094","city_name":"张家口"},{"city_code":"118095","city_name":"承德"},{"city_code":"118096","city_name":"沧州"},{"city_code":"118097","city_name":"廊坊"},{"city_code":"118098","city_name":"衡水"}],"province_name":"河北"},{"province_code":"108318","city":[{"city_code":"118410","city_name":"吐鲁番"},{"city_code":"118409","city_name":"克拉玛依"},{"city_code":"118415","city_name":"克孜勒苏"},{"city_code":"118421","city_name":"阿勒泰"},{"city_code":"131521","city_name":"五家渠"},{"city_code":"118408","city_name":"乌鲁木齐"},{"city_code":"118414","city_name":"喀什"},{"city_code":"118420","city_name":"塔城"},{"city_code":"118419","city_name":"伊犁"},{"city_code":"118413","city_name":"阿克苏"},{"city_code":"118418","city_name":"博尔塔拉"},{"city_code":"118412","city_name":"和田"},{"city_code":"131386","city_name":"图木舒克"},{"city_code":"118417","city_name":"昌吉"},{"city_code":"118411","city_name":"哈密"},{"city_code":"128496","city_name":"石河子"},{"city_code":"131385","city_name":"阿拉尔"},{"city_code":"118416","city_name":"巴音郭楞"}],"province_name":"新疆"},{"province_code":"108302","city":[{"city_code":"118225","city_name":"潍坊"},{"city_code":"118231","city_name":"莱芜"},{"city_code":"118236","city_name":"菏泽"},{"city_code":"118224","city_name":"东营"},{"city_code":"118230","city_name":"日照"},{"city_code":"118229","city_name":"泰安"},{"city_code":"118223","city_name":"枣庄"},{"city_code":"118235","city_name":"滨州"},{"city_code":"118228","city_name":"济宁"},{"city_code":"118222","city_name":"淄博"},{"city_code":"118234","city_name":"聊城"},{"city_code":"118227","city_name":"威海"},{"city_code":"118221","city_name":"青岛"},{"city_code":"118233","city_name":"德州"},{"city_code":"118226","city_name":"烟台"},{"city_code":"118220","city_name":"济南"},{"city_code":"118232","city_name":"临沂"}],"province_name":"山东"},{"province_code":"108313","city":[{"city_code":"118364","city_name":"拉萨"},{"city_code":"118365","city_name":"那曲"},{"city_code":"118366","city_name":"昌都"},{"city_code":"118367","city_name":"山南"},{"city_code":"118368","city_name":"日喀则"},{"city_code":"118369","city_name":"阿里"},{"city_code":"118370","city_name":"林芝"}],"province_name":"西藏"},{"province_code":"108297","city":[{"city_code":"118159","city_name":"南京"},{"city_code":"118165","city_name":"连云港"},{"city_code":"118171","city_name":"宿迁"},{"city_code":"118164","city_name":"南通"},{"city_code":"118170","city_name":"泰州"},{"city_code":"131562","city_name":"宜兴"},{"city_code":"118169","city_name":"镇江"},{"city_code":"118163","city_name":"苏州"},{"city_code":"118168","city_name":"扬州"},{"city_code":"118162","city_name":"常州"},{"city_code":"118167","city_name":"盐城"},{"city_code":"118161","city_name":"徐州"},{"city_code":"118166","city_name":"淮安"},{"city_code":"118160","city_name":"无锡"}],"province_name":"江苏"},{"province_code":"108309","city":[],"province_name":"重庆"},{"province_code":"108292","city":[{"city_code":"118112","city_name":"包头"},{"city_code":"118117","city_name":"呼伦贝尔"},{"city_code":"118111","city_name":"呼和浩特"},{"city_code":"118116","city_name":"鄂尔多斯"},{"city_code":"118122","city_name":"兴安盟"},{"city_code":"118115","city_name":"通辽"},{"city_code":"118121","city_name":"阿拉善盟"},{"city_code":"118114","city_name":"赤峰"},{"city_code":"118120","city_name":"锡林郭勒"},{"city_code":"118119","city_name":"乌兰察布"},{"city_code":"118113","city_name":"乌海"},{"city_code":"118118","city_name":"巴彦淖尔"}],"province_name":"内蒙古"},{"province_code":"108304","city":[{"city_code":"128466","city_name":"潜江"},{"city_code":"118266","city_name":"恩施"},{"city_code":"118254","city_name":"武汉"},{"city_code":"118260","city_name":"荆门"},{"city_code":"128465","city_name":"仙桃"},{"city_code":"118259","city_name":"宜昌"},{"city_code":"118265","city_name":"随州"},{"city_code":"128464","city_name":"天门"},{"city_code":"118258","city_name":"荆州"},{"city_code":"118264","city_name":"咸宁"},{"city_code":"118257","city_name":"十堰"},{"city_code":"118263","city_name":"黄冈"},{"city_code":"118256","city_name":"襄阳"},{"city_code":"118262","city_name":"孝感"},{"city_code":"130143","city_name":"神农架林区"},{"city_code":"118255","city_name":"黄石"},{"city_code":"118261","city_name":"鄂州"}],"province_name":"湖北"},{"province_code":"108288","city":[],"province_name":"北京"},{"province_code":"108299","city":[{"city_code":"118188","city_name":"淮北"},{"city_code":"118194","city_name":"宿州"},{"city_code":"118199","city_name":"宣城"},{"city_code":"118187","city_name":"马鞍山"},{"city_code":"118193","city_name":"阜阳"},{"city_code":"118198","city_name":"池州"},{"city_code":"118186","city_name":"淮南"},{"city_code":"118192","city_name":"滁州"},{"city_code":"118197","city_name":"亳州"},{"city_code":"118185","city_name":"蚌埠"},{"city_code":"118191","city_name":"黄山"},{"city_code":"118196","city_name":"六安"},{"city_code":"118184","city_name":"芜湖"},{"city_code":"118190","city_name":"安庆"},{"city_code":"118189","city_name":"铜陵"},{"city_code":"118183","city_name":"合肥"}],"province_name":"安徽"},{"province_code":"108315","city":[{"city_code":"118385","city_name":"嘉峪关"},{"city_code":"118391","city_name":"定西"},{"city_code":"118384","city_name":"天水"},{"city_code":"118390","city_name":"庆阳"},{"city_code":"118389","city_name":"酒泉"},{"city_code":"118383","city_name":"白银"},{"city_code":"118388","city_name":"平凉"},{"city_code":"118382","city_name":"金昌"},{"city_code":"118394","city_name":"甘南"},{"city_code":"118387","city_name":"张掖"},{"city_code":"118381","city_name":"兰州"},{"city_code":"118393","city_name":"临夏"},{"city_code":"118386","city_name":"武威"},{"city_code":"118392","city_name":"陇南"}],"province_name":"甘肃"},{"province_code":"108310","city":[{"city_code":"118318","city_name":"成都"},{"city_code":"118329","city_name":"宜宾"},{"city_code":"118324","city_name":"广元"},{"city_code":"118335","city_name":"资阳"},{"city_code":"118330","city_name":"广安"},{"city_code":"118326","city_name":"内江"},{"city_code":"118337","city_name":"甘孜"},{"city_code":"118321","city_name":"泸州"},{"city_code":"118332","city_name":"眉山"},{"city_code":"118328","city_name":"南充"},{"city_code":"118323","city_name":"绵阳"},{"city_code":"118334","city_name":"巴中"},{"city_code":"118319","city_name":"自贡"},{"city_code":"118325","city_name":"遂宁"},{"city_code":"118336","city_name":"阿坝"},{"city_code":"118320","city_name":"攀枝花"},{"city_code":"118331","city_name":"达州"},{"city_code":"118327","city_name":"乐山"},{"city_code":"118338","city_name":"凉山"},{"city_code":"118322","city_name":"德阳"},{"city_code":"118333","city_name":"雅安"}],"province_name":"四川"}]
     * state_code : 108088
     * state_name : 中国
     */

    private String state_code;
    private String state_name;
    private List<ProvinceBean> province;

    public String getState_code() {
        return state_code;
    }

    public void setState_code(String state_code) {
        this.state_code = state_code;
    }

    public String getState_name() {
        return state_name;
    }

    public void setState_name(String state_name) {
        this.state_name = state_name;
    }

    public List<ProvinceBean> getProvince() {
        return province;
    }

    public void setProvince(List<ProvinceBean> province) {
        this.province = province;
    }

    public static class ProvinceBean implements Serializable {
        /**
         * province_code : 108294
         * city : [{"city_code":"118137","city_name":"长春"},{"city_code":"118139","city_name":"四平"},{"city_code":"118140","city_name":"辽源"},{"city_code":"118141","city_name":"通化"},{"city_code":"118142","city_name":"白山"},{"city_code":"118143","city_name":"松原"},{"city_code":"118144","city_name":"白城"},{"city_code":"118145","city_name":"延边"},{"city_code":"129059","city_name":"吉林"}]
         * province_name : 吉林
         */

        private String province_code;
        private String province_name;
        private List<CityBean> city;

        public String getProvince_code() {
            return province_code;
        }

        public void setProvince_code(String province_code) {
            this.province_code = province_code;
        }

        public String getProvince_name() {
            return province_name;
        }

        public void setProvince_name(String province_name) {
            this.province_name = province_name;
        }

        public List<CityBean> getCity() {
            return city;
        }

        public void setCity(List<CityBean> city) {
            this.city = city;
        }

        public static class CityBean implements Serializable {
            /**
             * city_code : 118137
             * city_name : 长春
             */

            private String city_code;
            private String city_name;

            public String getCity_code() {
                return city_code;
            }

            public void setCity_code(String city_code) {
                this.city_code = city_code;
            }

            public String getCity_name() {
                return city_name;
            }

            public void setCity_name(String city_name) {
                this.city_name = city_name;
            }
        }
    }
}
