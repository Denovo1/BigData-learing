package com.dqsy.sparkproject.data;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import java.util.*;

public class BlackListData extends Thread {

    private static final Random random = new Random();
    private static final String[] provinces = "北京市,天津市,上海市,重庆市,河北省,山西省,台湾省,辽宁省,吉林省,黑龙江省,江苏省,浙江省,安徽省,福建省,江西省,山东省,河南省,湖北省,湖南省,广东省,甘肃省,四川省,贵州省,海南省,云南省,青海省,陕西省,广西壮族自治区,西藏自治区,宁夏回族自治区,新疆维吾尔自治区,内蒙古自治区,澳门特别行政区,香港特别行政区".split(",");
    private static final Map<String, String[]> provinceCityMap = new HashMap<String, String[]>();

    private Producer<Integer, String> producer;

    public BlackListData() {
        provinceCityMap.put(provinces[0], new String[]{"北京市"});
        provinceCityMap.put(provinces[1], new String[]{"天津市"});
        provinceCityMap.put(provinces[2], new String[]{"上海市"});
        provinceCityMap.put(provinces[3], new String[]{"重庆市"});
        provinceCityMap.put(provinces[4], "唐山市,衡水市,张家口市,秦皇岛市,承德市,邯郸市,沧州市,邢台市,石家庄市,廊坊市,保定市".split(","));
        provinceCityMap.put(provinces[5], "太原市,大同市,阳泉市,长治市,晋城市,朔州市,晋中市,运城市,忻州市,临汾市,吕梁市".split(","));
        provinceCityMap.put(provinces[6], "新竹县,南投县,高雄县,台中市,花莲县,台北县,苗栗县,云林县,台北市,屏东县,台南市,宜兰县,台中县,嘉义县,高雄市,澎湖县,新竹市,桃园县,彰化县,台南县,基隆市,台东县,嘉义市".split(","));
        provinceCityMap.put(provinces[7], "鞍山市,锦州市,盘锦市,抚顺市,营口市,铁岭市,沈阳市,本溪市,阜新市,朝阳市,大连市,丹东市,辽阳市,葫芦岛市".split(","));
        provinceCityMap.put(provinces[8], "辽源市,白城市,长春市,通化市,延边朝鲜族自治州,吉林市,白山市,四平市,松原市".split(","));
        provinceCityMap.put(provinces[9], "鹤岗市,伊春市,黑河市,双鸭山市,牡丹江市,绥化市,哈尔滨市,鸡西市,佳木斯市,大兴安岭地区,齐齐哈尔市,大庆市,七台河市".split(","));
        provinceCityMap.put(provinces[10], "南京市,苏州市,盐城市,宿迁市,无锡市,南通市,扬州市,徐州市,连云港市,镇江市,常州市,淮安市,泰州市".split(","));
        provinceCityMap.put(provinces[11], "温州市,金华市,丽水市,嘉兴市,衢州市,杭州市,湖州市,舟山市,宁波市,绍兴市,台州市".split(","));
        provinceCityMap.put(provinces[12], "铜陵市,阜阳市,亳州市,淮南市,安庆市,宿州市,池州市,合肥市,马鞍山市,黄山市,巢湖市,宣城市,芜湖市,淮北市,滁州市,六安市,蚌埠市".split(","));
        provinceCityMap.put(provinces[13], "泉州市,宁德市,厦门市,漳州市,莆田市,南平市,三明市,龙岩市,福州市".split(","));
        provinceCityMap.put(provinces[14], "九江市,吉安市,南昌市,新余市,宜春市,景德镇市,鹰潭市,抚州市,萍乡市,赣州市,上饶市".split(","));
        provinceCityMap.put(provinces[15], "枣庄市,济宁市,莱芜市,滨州市,济南市,东营市,泰安市,临沂市,菏泽市,青岛市,烟台市,威海市,德州市,淄博市,潍坊市,日照市,聊城市".split(","));
        provinceCityMap.put(provinces[16], "驻马店市,开封市,鹤壁市,许昌市,商丘市,济源市,洛阳市,新乡市,漯河市,信阳市,平顶山市,焦作市,三门峡市,周口市,郑州市,安阳市,濮阳市,南阳市".split(","));
        provinceCityMap.put(provinces[17], "天门市,十堰市,鄂州市,咸宁市,潜江市,荆州市,荆门市,随州市,武汉市,神农架林区,宜昌市,孝感市,仙桃市,黄石市,恩施土家族苗族自治州,襄樊市,黄冈市".split(","));
        provinceCityMap.put(provinces[18], "怀化市,长沙市,邵阳市,益阳市,娄底市,株洲市,岳阳市,郴州市,湘西土家族苗族自治州,湘潭市,常德市,永州市,衡阳市,张家界市".split(","));
        provinceCityMap.put(provinces[19], "汕尾市,东莞市,深圳市,云浮市,佛山市,肇庆市,河源市,中山市,珠海市,江门市,惠州市,阳江市,潮州市,汕头市,湛江市,梅州市,清远市,广州市,揭阳市,韶关市,茂名市".split(","));
        provinceCityMap.put(provinces[20], "定西市,天水市,平凉市,陇南市,兰州市,嘉峪关市,酒泉市,临夏回族自治州,金昌市,武威市,庆阳市,甘南藏族自治州,白银市,张掖市".split(","));
        provinceCityMap.put(provinces[21], "眉山市,雅安市,成都市,甘孜藏族自治州,德阳市,内江市,宜宾市,巴中市,自贡市,凉山彝族自治州,绵阳市,乐山市,广安市,资阳市,攀枝花市,广元市,南充市,达州市,阿坝藏族羌族自治州,泸州市,遂宁市".split(","));
        provinceCityMap.put(provinces[22], "毕节地区,遵义市,黔西南布依族苗族自治州,安顺市,黔东南苗族侗族自治州,贵阳市,铜仁地区,黔南布依族苗族自治州,六盘水市".split(","));
        provinceCityMap.put(provinces[23], "临高县,陵水黎族自治县,海口市,儋州市,澄迈县,白沙黎族自治县,保亭黎族苗族自治县,三亚市,文昌市,定安县,昌江黎族自治县,琼中黎族苗族自治县,五指山市,万宁市,屯昌县,乐东黎族自治县,琼海市,东方市".split(","));
        provinceCityMap.put(provinces[24], "大理白族自治州,曲靖市,丽江市,红河哈尼族彝族自治州,德宏傣族景颇族自治州,玉溪市,思茅市,西双版纳傣族自治州,怒江傈傈族自治州,保山市,临沧市,楚雄彝族自治州,迪庆藏族自治州,昆明市,昭通市,文山壮族苗族自治州".split(","));
        provinceCityMap.put(provinces[25], "西宁市,海南藏族自治州,海东地区,果洛藏族自治州,海北藏族自治州,玉树藏族自治州,黄南藏族自治州,海西蒙古族藏族自治州".split(","));
        provinceCityMap.put(provinces[26], "咸阳市,榆林市,西安市,渭南市,安康市,铜川市,延安市,商洛市,宝鸡市,汉中市".split(","));
        provinceCityMap.put(provinces[27], "玉林市,来宾市,柳州市,防城港市,百色市,崇左市,桂林市,钦州市,贺州市,梧州市,贵港市,河池市,南宁市,北海市".split(","));
        provinceCityMap.put(provinces[28], "昌都地区,林芝地区,山南地区,拉萨市,日喀则地区,那曲地区,阿里地区".split(","));
        provinceCityMap.put(provinces[29], "吴忠市,固原市,银川市,中卫市,石嘴山市".split(","));
        provinceCityMap.put(provinces[30], "博乐市,克拉玛依市,乌苏市,五家渠市,哈密市,昌吉市,伊宁市,石河子市,阿勒泰市,吐鲁番市,和田市,阜康市,奎屯市,阿拉尔市,阿克苏市,阿图什市,米泉市,乌鲁木齐市,塔城市,图木舒克市,喀什市,库尔勒市".split(","));
        provinceCityMap.put(provinces[31], "锡林郭勒盟,乌海市,呼伦贝尔市,兴安盟,赤峰市,巴彦淖尔市,阿拉善盟,呼和浩特市,通辽市,乌兰察布市,包头市,鄂尔多斯市".split(","));
        provinceCityMap.put(provinces[32], "澳门特别行政区".split(","));
        provinceCityMap.put(provinces[33], "香港特别行政区".split(","));

        producer = new Producer<Integer, String>(createProducerConfig());
    }

    private ProducerConfig createProducerConfig() {
        Properties props = new Properties();
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        props.put("metadata.broker.list", "master:9092,slave1:9092,slave2:9092");
        return new ProducerConfig(props);
    }

    public void run() {
        while (true) {
            String log = new Date().getTime() + " " + "北京市" + " " + "北京市" + " "
                    + 1002 + " " + 11;
            producer.send(new KeyedMessage<Integer, String>("AdRealTimeLog", log));

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 启动Kafka Producer
     *
     * @param args
     */
    public static void main(String[] args) {
        BlackListData producer = new BlackListData();
        producer.start();
    }

}
