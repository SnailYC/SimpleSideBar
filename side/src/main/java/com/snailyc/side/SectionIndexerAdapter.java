package com.snailyc.side;

/**
 * 提供有关侧边索引集合的信息。
 */
public interface SectionIndexerAdapter {
    /**
     * 返回索引的数量
     *
     * 例如按照字母作为索引，假设每个字母相关的数据至少出现一次，那么应该返回26
     */
    int getSectionCount();

    /**
     * 返回该索引位置的标题字符
     *
     * @param position 仅代表字母索引数量中的第几个字母
     */
    String getSectionTitle(int position);

    /**
     * 返回一个整数，它描述与某个索引相关联的数据集的大小。
     * 返回值与其它部分的大小作比较，得出在绘制时索引的位置。
     * 返回值必须大于0，如果不想显示某个索引，应该在{@link #getSectionCount（）}中返回可见部分的数量
     *
     * @param position 仅代表字母索引数量中的第几个字母
     */
    int getSectionWeight(int position);
}
