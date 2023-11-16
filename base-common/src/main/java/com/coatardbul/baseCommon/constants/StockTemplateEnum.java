package com.coatardbul.baseCommon.constants;

public enum StockTemplateEnum {

    TWO_UP_LIMIT_ABOVE_CALL_AUCTION("TWO_UP_LIMIT_ABOVE_CALL_AUCTION", "连涨2天以上集合竞价", "1481302460344696832"),
    INDUSTRY("INDUSTRY", "所属行业", "1661297962061529088"),
    STOCK_DETAIL("STOCK_DETAIL", "股票详情", "1515522893696598016"),
    FIRST_UP_LIMIT_WATCH_TWO("FIRST_UP_LIMIT_WATCH_TWO", "首次涨停（查看二板）", "1506450265249808384"),
    HAVE_UP_LIMIT("HAVE_UP_LIMIT", "昨曾模式", "1501584345410961408"),
    UP_LIMIT("UP_LIMIT", "非st涨停", "1491466098237898752"),
    FIRST_UP_LIMIT("FIRST_UP_LIMIT", "首板涨停", "1518525708752781312"),

    STEADY_THREE_INCREASE_BIG_SMALL_BIG("STEADY_THREE_INCREASE_BIG_SMALL_BIG", "稳中有升--大阳-？-大阳", "1528045774859010048"),
    FIRST_UP_LIMIT_WATCH_TWO_SUPPLEMENT_AMPLITUDE("FIRST_UP_LIMIT_WATCH_TWO_SUPPLEMENT_AMPLITUDE", "首次涨停（查看二板）补充振幅数据", "1549066033128669184"),
    AUCTION_GREATE5("AUCTION_GREATE5", "竞价涨幅大于5（无日线）", "1656557975323672576"),
    STEADY_THREE_INCREASE_UP_BIG_SMALL_BIG("STEADY_THREE_INCREASE_UP_BIG_SMALL_BIG", "稳中有升（大大阳-小阳-大阳）", "1526961477527928832"),
    DOWN_LIMIT("DOWN_LIMIT", "主板跌停（包括一字跌停）", "1491463259323236352"),
    STOCK_UP_LIMIT("STOCK_UP_LIMIT", "股票涨停信息", "1509349533765730304"), a13("a13", "分型突破", "1499296750098317312"),
    OUTSIDE_UP_LIMIT("OUTSIDE_UP_LIMIT", "外侧模式，涨停预警", "1501588045365903360"),
    PARTING_UP_LIMIT("PARTING_UP_LIMIT", "分型涨停（专门查看使用）", "1501926185377071104"),
    a16("a16", "竞价抢筹", "1645356839652687872"),
    STEADY_THREE_INCREASE_UP_SMALL_DOWN_BIG_UP_BIG("STEADY_THREE_INCREASE_UP_SMALL_DOWN_BIG_UP_BIG", "稳中有升（小阳-大阴-大阳）", "1530752831227822080"),
    a18("a18", "竞价涨幅大于5", "1655471684272128000"),
    AUCTION_GREATE2("AUCTION_GREATE2", "竞价涨幅大于2", "1657970527345704960"),
    STEADY_THREE_INCREASE_UP_DOWN_UP("STEADY_THREE_INCREASE_UP_DOWN_UP", "稳中有升（大大阳-阴-阳）", "1528711135640027136"),
    FOR_EACH_STOCK("FOR_EACH_STOCK", "遍历股票名称", "1567739221362475008"),
    INCREASE_GREATE("INCREASE_GREATE", "涨幅大于7主板", "1602306202841251840"),
    BREAK_SWORD("BREAK_SWORD", "破剑式（喇叭口出击）", "1502896274603638784"),
    TWO_PLATE_HIGH_EXPECT("TWO_PLATE_HIGH_EXPECT", "二板烂板高预期", "1505216687434235904"),
    a25("a25", "昨日涨停（计算方差，标准差）", "1481232028362997760"),
    THEME_AUCTION("THEME_AUCTION", "题材竞价", "1520443939201613824"),
    SIMILAR_HAVE_UP_LIMIT("SIMILAR_HAVE_UP_LIMIT", "类似昨曾", "1630271102188126208"),
    a28("a28", "外侧模式振幅10个点，5亿", "1477323946260037632"),
    TWO_UP_LIMIT_ABOVE("TWO_UP_LIMIT_ABOVE", "连涨2天以上", "1492575555046998016"),
    FIRST_UP_LIMIT_STRONG_THREE_WEAK("FIRST_UP_LIMIT_STRONG_THREE_WEAK", "二板强，三板弱", "1528230452060618752"),
    XXX("XXX", "首板涨停，查看二板，提前看", "1564604427158028288"),
    INCREASE_GREATE5("INCREASE_GREATE5", "涨幅大于5", "1671075952727293952"),
    FIRST_UP_LIMIT_ONE_WORD("FIRST_UP_LIMIT_ONE_WORD", "首板一字", "1505911842550185984"),
    WASH_PULL("WASH_PULL", "洗拉（上下影洗盘突破拉升）", "1621087291382562816"),
    DOWN_LEVEL_4("DOWN_LEVEL_4", "涨幅小于-7未跌停", "1524958785226014720"),
    FIRST_UP_LIMIT_TIME_SMALL("FIRST_UP_LIMIT_TIME_SMALL", "首板烂板，二板高开（封板时长）", "1528344786329796608"),
    HIGH_TURN_OVER_UP_LIMIT("HIGH_TURN_OVER_UP_LIMIT", "高换手涨停", "1501580937186639872"),
    a38("a38", "稳中有升（阴-阴-大阳）", "1535957999556886528"),
    非st板块("非st板块", "测试1", "1562664891175796736"),
    a40("a40", "昨曾，第二天向上收红", "1568924594423857152"),
    a41("a41", "测试涨停--空一日--涨幅大于9", "1615992487707541504"),
    LOW_AUCTION_SHORT_DOWN_LONG_UP_SHADOW("LOW_AUCTION_SHORT_DOWN_LONG_UP_SHADOW", "低开上下影", "1620450980170694656"),
    a43("a43", "首板涨停+长下影线", "1623326923188994048"),
    a44("a44", "测试三阳小振幅", "1626587066139803648"),
    a45("a45", "注册制集合竞价", "1643150591406505984"),
    a46("a46", "创业板高均价", "1646145363289178112"),
    a47("a47", "跌停后的反转", "1658060802206334976"),
    AUCTION_LESS_F2("AUCTION_LESS_F2", "竞价涨幅小于-2", "1659442484557774848"),
    INDUSTRY_UPLIMIT("INDUSTRY_UPLIMIT", "涨停+行业", "1661312669275258880"),
    a50("a50", "所属行业（上午）", "1679329408747438080"),
    INCREASE_LESS_F5("INCREASE_LESS_F5", "涨幅小于-5", "1681108269805993984"),
    STOCK_DOWN_LIMIT("STOCK_DOWN_LIMIT", "股票跌停信息", "1515527745369669632"),
    a53("a53", "海底捞月", "1611396475558952960"),
    LOW_AUCTION_UP_SHADOW("LOW_AUCTION_UP_SHADOW", "低开上影线", "1617067872272646144"),
    a55("a55", "上升三法", "1618923399483097088"),
    a56("a56", "昨曾+涨停", "1619178606024589312"),
    a57("a57", "昨曾+实体阴线", "1623455716696260608"),
    a58("a58", "测试阳？阳（测试）", "1658317872797188096"),
    a59("a59", "所属行业（过滤集中度）", "1680909970444517376"),
    a60("a60", "macd上移", "1703987719866286080"),
    a61("a61", "连涨2天", "1480203054681817088"),
    OUTSIDE_AMPLITUDE_LOW_AMOUNT("OUTSIDE_AMPLITUDE_LOW_AMOUNT", "外侧模式振幅8个点，1亿", "1480918944930988032"),
    a63("a63", "二板栏板分歧板", "1506055241966157824"),
    a64("a64", "连涨2天（计算方差，标准差）", "1512591115088429056"),
    a65("a65", "连涨3天以上（计算标准差，方差）", "1512592043300487168"),
    DOWN_LEVEL_2("DOWN_LEVEL_2", "涨幅大于等于-5小于-3", "1524962678521593856"),
    LEVEL_0("LEVEL_0", "涨幅等于0", "1524963121909858304"),
    DOWN_LEVEL_5("DOWN_LEVEL_5", "涨幅跌停", "1524998261579055104"),
    a69("a69", "开板次数大于8次", "1526980346908901376"),
    STEADY_THREE_INCREASE_UPLIMIT_UP_UP("STEADY_THREE_INCREASE_UPLIMIT_UP_UP", "稳中有升（涨停--小阳--小阳）", "1528768619469733888"),
    a71("a71", "测试22开板次数大的", "1568922159802351616"),
    a72("a72", "涨幅测试", "1595706588503605248"),
    a73("a73", "假摔", "1610677038291746816"),
    a74("a74", "4阳加涨停", "1611955731492372480"),
    a75("a75", "长阳不破7日", "1612054878266458112"),
    a76("a76", "测试二板T字", "1614665435981152256"),
    a77("a77", "测试涨停第四天", "1617119328283525120"),
    a78("a78", "测试昨曾（阴）+红", "1619597683356467200"),
    a79("a79", "测试昨曾+外侧", "1619676850039160832"),
    a80("a80", "首板涨停+均线上移", "1633069115109408768"),
    a81("a81", "阳阴阳", "1643246269646372864"),
    AMBUSH_CALLAUCTION_ROB("AMBUSH_CALLAUCTION_ROB", "埋伏过滤竞价抢筹", "1645760659029360640"),
    a83("a83", "测试11111111111111", "1646493597400432640"),
    a84("a84", "测试123", "1658787784242102272"),
    ZCK_UPLIMIT("ZCK_UPLIMIT", "主板，创业板，科创板涨停", "1659089442239021056"),
    a86("a86", "所属行业+分时换手", "1699994599839367168"),
    a87("a87", "连涨3天", "1480198675199295488"),
    a88("a88", "创业板，科创板，昨日涨停", "1483032575030198272"),
    a89("a89", "主板炸板", "1491464964349755392"),
    a90("a90", "主板炸板未回封", "1491467156385300480"),
    TWO_PLATE_QUICK_TO_UP("TWO_PLATE_QUICK_TO_UP", "二板高预期快速冲板", "1505357737901555712"),
    a92("a92", "明日涨停", "1510610979782787072"),
    a93("a93", "振幅大，快涨停", "1516434023130464256"), a94("a94", "题材全量", "1521529988216651776"), UP_LEVEL_1("UP_LEVEL_1", "涨幅大于0小于等于3", "1524963351229235200"), a96("a96", "涨停-阴-阴", "1538501430363684864"), a97("a97", "测试55", "1568981243725479936"), a98("a98", "首板烂板，二板独孤一剑", "1611746244139548672"), a99("a99", "长阳不破", "1611759022883602432"), a100("a100", "测试涨停，一字，继续涨", "1614659242621534208"), a101("a101", "测试烂烂", "1614688010073407488"), a102("a102", "测试烂烂高开", "1614693270477209600"), a103("a103", "测试二板以上首阴", "1614993160914731008"), a104("a104", "二板一字，三板低开", "1619698124601819136"), a105("a105", "测试跌幅大的", "1619707549236461568"), a106("a106", "大单小单", "1621438587805499392"), a107("a107", "涨幅大于9+下影线", "1623656868490641408"), a108("a108", "类似昨曾+外侧", "1632181924581736448"), a109("a109", "首板涨停+小振幅", "1632885572257316864"), CY_BIG_INCREASE_RATE("CY_BIG_INCREASE_RATE", "创业板大涨幅", "1637473715228901376"), a111("a111", "macd底背离", "1650528418120466432"), a112("a112", "稳中有升（阳+阳）", "1651984127106351104"), a113("a113", "非st板块涨幅排序", "1664151007875170304"), a114("a114", "测试模型", "1682631828890058752"),
    INCREASE_GREATE9_5("INCREASE_GREATE9_5", "涨幅大于9.5", "1687388954149650432"), a116("a116", "所属行业+macd", "1718835692093702144"), a117("a117", "测试-66666--创业板", "1722141346514468864"), a118("a118", "测试-666-主板", "1722142221903462400"), a119("a119", "连涨4天以上", "1480198844502376448"), a120("a120", "昨日涨停,非一字（计算方差，标准差）", "1482611456372506624"), a121("a121", "主板一字跌停", "1491461635838181376"), a122("a122", "上涨个数（只算当日）", "1492577515351441408"), a123("a123", "下跌个数（只算当日）", "1492577632515129344"), a124("a124", "隔空剑", "1502896170119331840"), MOVING_AVERAGES("MOVING_AVERAGES", "5日10日均线上扬", "1508081049291325440"), DOWN_LEVEL_3("DOWN_LEVEL_3", "涨幅大于等于-7小于-5", "1524962025799811072"), DOWN_LEVEL_1("DOWN_LEVEL_1", "涨幅大于等于-3小于0", "1524963034039189504"), UP_LEVEL_2("UP_LEVEL_2", "涨幅大于3小于等于5", "1524963433672474624"), UP_LEVEL_3("UP_LEVEL_3", "涨幅大于5小于等于7", "1524963486831083520"), UP_LEVEL_4("UP_LEVEL_4", "涨幅大于7未涨停", "1524963614182735872"), UP_LEVEL_5("UP_LEVEL_5", "涨幅涨幅", "1524998207392841728"), STEADY_THREE_INCREASE_WIDTH_RANGE("STEADY_THREE_INCREASE_WIDTH_RANGE", "稳中有升--（大阳-阳-阳）", "1528043445665529856"), FIRST_UP_LIMIT_OPEN_NUM_BIG("FIRST_UP_LIMIT_OPEN_NUM_BIG", "首板烂板，二板高开（打开涨停次数）", "1528344478283333632"), STOCK_DETAIL_SUP("STOCK_DETAIL_SUP", "股票详情_补充", "1591742776444321792"), a135("a135", "测试高开涨停，二板低开", "1614679380590723072"), a136("a136", "连续5天涨停及以上", "1616037845418508288"), a137("a137", "测试昨曾+翻红", "1619378091161944064"), a138("a138", "连板首阴", "1619556304379052032"), a139("a139", "扩大版（低开上影下影）", "1622613444723343360"), a140("a140", "测试均线", "1623259257313034240"), INCREASE_GREATE_NO_UPLIMIT("INCREASE_GREATE_NO_UPLIMIT", "涨幅大于9.5未涨停", "1623843021349060608"), a142("a142", "类似昨曾+小振幅", "1632172233856253952"), SIMILAR_HAVE_UP_LIMIT_SUPPLEMENT("SIMILAR_HAVE_UP_LIMIT_SUPPLEMENT", "类似昨曾补充（大价格）", "1634187319206608896"), a144("a144", "创业板火箭起飞", "1638175474171641856"), MACD_FILTER("MACD_FILTER", "股票详情（关于macd）", "1650454145674641408"), a146("a146", "竞价涨幅大于1", "1659427820310495232"), a147("a147", "竞价涨幅大于0.5", "1660512228559945728"), a148("a148", "竞价涨幅大于0", "1660537252524195840"), a149("a149", "竞价涨幅小于0", "1661289620102578176"), a150("a150", "竞价涨幅小于-1", "1661291848553070592"), a151("a151", "涨幅大于8", "1696365600567328768"), a152("a152", "下跌个数", "1480921345708654592"), a153("a153", "上涨个数", "1481166585669484544"), a154("a154", "连涨2天以上（计算方差，标准差）", "1483037371107770368"), a155("a155", "查看自选板块竞价数据", "1483760841617702912"), a156("a156", "主板炸板回封", "1491465472548405248");


    private String sign;

    private String desc;

    private String id;

    StockTemplateEnum(String sign, String desc, String id) {
        this.sign = sign;
        this.desc = desc;
        this.id = id;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
