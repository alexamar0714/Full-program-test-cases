/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sundar.jwt;

import com.sundar.jwt.utils.KeyGenerator;
import java.security.Key;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author sundar
 * @since 2017-10-28
 * @modified 2017-10-28
 */
public class KeyGeneratorImpl implements KeyGenerator {

    @Override
    public Key generateKey() {
        String keyString = "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="782c494a22194c4f12384b0120">[email protected]</a><a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="0b544f4675534b43">[email protected]</a>!imM]Lwf/,?KTSU^<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="bbfffb8d">[email protected]</a>";
        Key key = new SecretKeySpec(keyString.getBytes(), 0, keyString.getBytes().length, "DES");
        return key;
    }
}
