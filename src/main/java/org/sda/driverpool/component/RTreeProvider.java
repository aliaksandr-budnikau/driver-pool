package org.sda.driverpool.component;

import org.sda.driverpool.entity.RTreeIndex;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

interface RTreeProvider {
    RTreeIndex get(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException;
}