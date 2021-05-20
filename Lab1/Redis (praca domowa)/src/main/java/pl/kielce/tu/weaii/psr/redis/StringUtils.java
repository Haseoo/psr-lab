package pl.kielce.tu.weaii.psr.redis;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtils {
    public static boolean isNullNorEmpty(String s) {
        return s != null && !s.isEmpty() && !s.isBlank();
    }
    public static final String LUA_SCRIPT = "local grades = redis.call('hscan', ARGV[1], '0');local sum = 0.0;local sumEcts = 0.0;local currEcts = 0;for i, key in ipairs(grades[2]) do if ( i % 2 == 1) then currEcts = tonumber(redis.call('hget', key, 'ects')); sumEcts = sumEcts + currEcts; else sum = sum + tonumber(key) * currEcts; end; end; if(sumEcts == 0.0) then return 'No grades'; else return tostring(sum / sumEcts); end;";
}
