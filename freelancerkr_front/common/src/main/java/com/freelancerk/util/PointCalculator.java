package com.freelancerk.util;

import com.freelancerk.domain.User;

public class PointCalculator {

    public static int getPoint(int totalOptionMoney, User.Role role) {
        if (User.Role.ROLE_CLIENT.equals(role)) return (int) (totalOptionMoney*0.05);
        return (int) (totalOptionMoney*0.10);
    }
}
