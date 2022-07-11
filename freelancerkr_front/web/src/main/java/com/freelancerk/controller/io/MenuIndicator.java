package com.freelancerk.controller.io;

import com.freelancerk.domain.MenuHit;
import lombok.Data;

@Data
public class MenuIndicator {

    private MenuHit.ParentMenuCode parentMenuCode;
    private MenuHit.MenuCode menuCode;
    private boolean on;
}
