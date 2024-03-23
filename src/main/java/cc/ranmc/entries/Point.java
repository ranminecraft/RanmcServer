package cc.ranmc.entries;

import cc.ranmc.bean.RankItem;
import cc.ranmc.constant.Prams;
import cc.ranmc.util.DataFile;
import cn.hutool.json.JSONObject;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class Point {

    @Getter
    private static JSONObject json;

    /**
     * 初始化
     */
    public Point() {
        load();
    }

    /**
     * 从文件读取数据
     */
    public void load() {
        json = new JSONObject(DataFile.read("point"));
        DataFile.write("backup", json.toString());
    }

    /**
     * 保存数据到文件
     */
    public void save() {
        DataFile.write("point", json.toString());
    }

    /**
     * 查询积分
     * @param id QQ
     * @return 积分
     */
    public int check(long id) {
        if (!json.containsKey(String.valueOf(id))) return 0;
        return json.getJSONObject(String.valueOf(id)).getInt("value");
    }

    public int check(String id) {
        try {
            return check(Long.parseLong(id));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * 积分增加
     * @param id QQ
     * @param value 积分
     */
    public void plus(long id, int value) {
        JSONObject obj;
        if (json.containsKey(String.valueOf(id))) {
            obj = json.getJSONObject(String.valueOf(id));
        } else {
            obj = new JSONObject();
            obj.set(Prams.VALUE, 0);
            obj.set(Prams.DATE, "0");
        }
        value += obj.getInt(Prams.VALUE);
        obj.set(Prams.VALUE, value);
        json.set(String.valueOf(id), obj);
        DataFile.write("point", json.toString());
        save();
    }

    /**
     * 积分减少
     * @param id QQ
     * @param value 积分
     * @return 是否成功
     */
    public boolean sub(long id, int value) {
        JSONObject obj;
        if (json.containsKey(String.valueOf(id))) {
            obj = json.getJSONObject(String.valueOf(id));
        } else {
            return false;
        }
        int point = obj.getInt(Prams.VALUE);
        if (value > point) return false;
        point -= value;
        obj.set(Prams.VALUE, point);
        json.set(String.valueOf(id), obj);
        DataFile.write("point", json.toString());
        save();
        return true;
    }

    /**
     * 获取排名
     * @param id QQ
     * @return 排名
     */
    public int getRank(long id) {
        return getRank(String.valueOf(id));
    }

    public int getRank(String id) {
        int rank = 0;
        List<RankItem> rankList = new ArrayList<>();
        for (String key : json.keySet()) {
            rankList.add(new RankItem(key, ((JSONObject)json.get(key)).getInt("value")));
        }
        if (!rankList.isEmpty()) {
            rankList.sort((o1, o2) -> Integer.compare(o2.getValue(), o1.getValue()));
        } else {
            return rank;
        }
        for (RankItem item : rankList) {
            rank++;
            if (item.getName().equals(id)) break;
        }
        return rank;
    }

    public String getRankList() {
        List<RankItem> rankList = new ArrayList<>();
        for (String key : json.keySet()) {
            rankList.add(new RankItem(key, ((JSONObject)json.get(key)).getInt("value")));
        }
        if (!rankList.isEmpty()) {
            rankList.sort((o1, o2) -> Integer.compare(o2.getValue(), o1.getValue()));
        } else {
            return "null";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            if ((i + 1) > rankList.size()) {
                break;
            }
            RankItem item = rankList.get(i);
            builder.append("(").append(i + 1).append(") QQ").append(item.getName()).append(" -> ").append(item.getValue()).append("积分\n");
        }
        return builder.toString();
    }
}
