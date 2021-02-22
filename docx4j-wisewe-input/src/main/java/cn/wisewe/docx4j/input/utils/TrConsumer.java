package cn.wisewe.docx4j.input.utils;

/**
 * 3个参数的消费者
 * @author xiehai
 * @date 2021/01/05 21:05
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
@FunctionalInterface
public interface TrConsumer<P1, P2, P3> {
    /**
     * 消费方法
     * @param p1 参数一
     * @param p2 参数二
     * @param p3 参数三
     */
    void accept(P1 p1, P2 p2, P3 p3);
}
