package kr.ac.kopo.moneylog.data;

import java.util.ArrayList;

import kr.ac.kopo.moneylog.model.Transaction;

public class DummyData {

    public static ArrayList<Transaction> getDummyData() {

        ArrayList<Transaction> list = new ArrayList<>();

        list.add(
                new Transaction(
                        "수입",
                        2500000,
                        "급여",
                        "8월 월급",
                        "2024-08-05"));

        list.add(
                new Transaction(
                        "지출",
                        12000,
                        "식비",
                        "점심",
                        "2024-08-10"));

        list.add(
                new Transaction(
                        "지출",
                        7900,
                        "카페",
                        "테이크아웃",
                        "2024-08-12"));

        list.add(
                new Transaction(
                        "지출",
                        1500,
                        "교통",
                        "버스",
                        "2024-08-15"));

        list.add(
                new Transaction(
                        "지출",
                        80000,
                        "쇼핑",
                        "옷 구매",
                        "2024-08-18"));

        list.add(
                new Transaction(
                        "지출",
                        30000,
                        "문화",
                        "영화",
                        "2024-08-20"));

        return list;
    }
}