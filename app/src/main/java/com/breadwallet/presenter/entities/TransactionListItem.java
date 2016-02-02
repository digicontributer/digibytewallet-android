package com.breadwallet.presenter.entities;

/**
 * BreadWallet
 * <p/>
 * Created by Mihail on 1/13/16.
 * Copyright (c) 2016 Mihail Gutan <mihail@breadwallet.com>
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
public class TransactionListItem {
    public static final String TAG = TransactionListItem.class.getName();
    final protected static char[] hexArray = "0123456789abcdef".toCharArray();
    private long timeStamp;
    private long blockHeight;
    private String hexId;
    private long sent;
    private long received;
    private long fee;
    private String to;
    private String from;
    private long balanceAfterTx;

    private TransactionListItem() {
    }

    public TransactionListItem(long timeStamp, long blockHeight, byte[] hash, long sent,
                               long received, long fee, String to, String from, long balanceAfterTx) {
        this.timeStamp = timeStamp;
        this.blockHeight = blockHeight;
        this.hexId = bytesToHex(hash);
        this.sent = sent;
        this.received = received;
        this.fee = fee;
        this.to = to;
        this.from = from;
        this.balanceAfterTx = balanceAfterTx;
//        Log.e(TAG, "TransactionListItem CONSTRUCTOR!");
//        CustomLogger.LogThis("timeStamp", String.valueOf(timeStamp), "blockHeight", String.valueOf(blockHeight),
//                "hash", hexId, "sent", String.valueOf(sent), "received", String.valueOf(received),
//                "fee", String.valueOf(fee), "to", to, "from", from);
    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public long getBlockHeight() {
        return blockHeight;
    }

    public long getFee() {
        return fee;
    }

    public String getFrom() {
        return from;
    }

    public static char[] getHexArray() {
        return hexArray;
    }

    public String getHexId() {
        return hexId;
    }

    public long getReceived() {
        return received;
    }

    public long getSent() {
        return sent;
    }

    public static String getTAG() {
        return TAG;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public String getTo() {
        return to;
    }

    public long getBalanceAfterTx(){
        return balanceAfterTx;
    }

}