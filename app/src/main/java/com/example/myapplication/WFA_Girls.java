package com.example.myapplication;

public class WFA_Girls {
    public String WFA_Girls_M(int age, double weight){
        String status = "Null";
        double[] negasd3 = {
                2.0, 2.7, 3.4, 4.4, 4.4, 4.8,
                5.1, 5.3, 5.6, 5.8, 5.9, 6.1,
                6.3, 6.4, 6.6, 6.7, 6.9, 7.0,
                7.2, 7.3, 7.5, 7.6, 7.8, 7.9,
                8.1, 8.2, 8.4, 8.5, 8.6, 8.8,
                8.9, 9.0, 9.1, 9.3, 9.4, 9.5,
                9.6, 9.7, 9.8, 9.9, 10.1, 10.2,
                10.3, 10.4, 10.5, 10.6, 10.7, 10.8,
                10.9, 11.0, 11.1, 11.2, 11.3, 11.4,
                11.5, 11.6, 11.7, 11.8, 11.9, 12.0
        };
        double[] negasd2 = {
                2.4, 3.2, 3.9, 4.5, 5.0, 5.4,
                5.7, 6.0, 6.3, 6.5, 6.7, 6.9,
                7.0, 7.2, 7.4, 7.6, 7.7, 7.9,
                8.1, 8.2, 8.4, 8.6, 8.7, 8.9,
                9.0, 9.2, 9.4, 9.5, 9.7, 9.8,
                10.0, 10.1, 10.3, 10.4, 10.5, 10.7,
                10.8, 10.9, 11.1, 11.2, 11.3, 11.5,
                11.6, 11.7, 11.8, 12.0, 12.1, 12.2,
                12.3, 12.4, 12.6, 12.7, 12.8, 12.9,
                13.0, 13.2, 13.3, 13.4, 13.5, 13.6
        };
        double[] negasd1 = {
                2.8, 3.6, 4.5, 5.2, 5.7, 6.1,
                6.5, 6.8, 7.7, 7.3, 7.5, 7.7,
                7.9, 8.1, 8.3, 8.5, 8.7, 8.9,
                9.1, 9.2, 9.4, 9.6, 9.8, 10.0,
                10.2, 10.3, 10.5, 10.7, 10.9, 11.1,
                11.2, 11.4, 11.6, 11.7, 11.9, 12.1,
                12.2, 12.4, 12.5, 12.7, 12.8, 13.1,
                13.1, 13.3, 13.4, 13.6, 13.7, 13.9,
                14.0, 14.2, 14.3, 14.5, 14.6, 14.8,
                14.9, 15.1, 15.2, 15.3, 15.5, 15.6
        };
        double[] median = {
                3.2, 4.2, 5.1, 5.8, 6.4, 6.9,
                7.3, 7.6, 7.9, 8.2, 8.5, 8.7,
                8.9, 9.2, 9.4, 9.6, 9.8, 10.0,
                10.2, 10.4, 10.6, 10.9, 11.1, 11.3,
                11.5, 11.7, 11.9, 12.1, 12.3, 12.5,
                12.7, 12.9, 13.1, 13.3, 13.5, 13.7,
                13.9, 14.0, 14.2, 14.4, 14.6, 14.8,
                15.0, 15.2, 15.3, 15.5, 15.7, 15.9,
                16.1, 16.3, 16.4, 16.6, 16.8, 17.0,
                17.2, 17.3, 17.5, 17.7, 17.9, 18.0
        };
        double[] posisd1 = {
                3.7, 4.8, 5.8, 6.6, 7.3, 7.8,
                8.2, 8.6, 9.0, 9.3, 9.6, 9.9,
                10.1, 10.4, 10.6, 10.9, 11.1, 11.4,
                11.6, 11.8, 12.1, 12.3, 12.5, 12.8,
                13.0, 13.3, 13.5, 13.7, 14.0, 14.2,
                14.4, 14.7, 14.9, 15.1, 15.4, 15.6,
                15.8, 16.0, 16.3, 16.5, 16.7, 16.9,
                17.2, 17.4, 17.6, 17.8, 18.1, 18.3,
                18.5, 18.8, 19.0, 19.2, 19.4, 19.7,
                19.9, 20.1, 20.3, 20.6, 20.8, 21.1
        };
        double[] posisd2 = {
                4.2, 5.5, 6.6, 7.5, 8.2, 8.8, 9.3,
                9.8, 10.2, 10.5, 10.9, 11.2, 11.5, 11.8,
                12.1, 12.4, 12.6, 12.9, 13.2, 13.5, 13.7,
                14.0, 14.3, 14.6, 14.8, 15.1, 15.4, 15.7,
                16.0, 16.2, 16.5, 16.8, 17.1, 17.3, 17.6,
                17.9, 18.1, 18.4, 18.7, 19.0, 19.2, 19.5,
                19.8, 20.1, 20.4, 20.7, 20.9, 21.2, 21.5,
                21.8, 22.1, 22.4, 22.6, 22.9, 23.2, 23.5,
                23.8, 24.1, 24.4,24.6
        };
        double[] posisd3 ={
                4.8, 6.2, 7.5, 8.5, 9.3, 10.0,
                10.6, 11.1, 11.6, 12.0, 12.4, 12.8,
                13.1, 13.5, 13.8, 14.1, 14.5, 14.8,
                15.1, 15.4, 15.7, 16.0, 16.4, 16.7,
                17.0, 17.3, 17.7, 18.0, 18.3, 18.7,
                19.0, 19.3, 19.6, 20.0, 20.3, 20.6,
                20.9, 21.3, 21.6, 22.0, 22.3, 22.7,
                23.0, 23.4, 23.7, 24.1, 24.5, 24.8,
                25.2, 25.5, 25.9, 26.3, 26.6, 27.0,
                27.4, 27.7, 28.1, 28.5, 28.8, 29.2
        };

        FindStatusWFA findStatusWFA = new FindStatusWFA();
        status = findStatusWFA.StatusFinder(age, weight, negasd3, negasd2, negasd1, median, posisd1, posisd2, posisd3);
        return status;

}
}