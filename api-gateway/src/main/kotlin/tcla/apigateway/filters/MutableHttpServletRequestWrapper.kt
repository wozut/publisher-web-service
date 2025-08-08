package tcla.apigateway.filters

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import java.util.Collections
import java.util.Enumeration


class MutableHttpServletRequestWrapper(request: HttpServletRequest) : HttpServletRequestWrapper(request) {

    private val customHeaders: MutableMap<String, String> = HashMap()
    fun putHeader(name: String, value: String) {
        customHeaders[name] = value
    }

    override fun getHeader(name: String): String? =
        customHeaders[name] ?: (request as HttpServletRequest).getHeader(name)

    override fun getHeaderNames(): Enumeration<String> {
        val set: MutableSet<String> = HashSet(customHeaders.keys)

        val enumeration: Enumeration<String> = (request as HttpServletRequest).headerNames
        while (enumeration.hasMoreElements()) {
            set.add(enumeration.nextElement())
        }

        return Collections.enumeration(set)
    }

    override fun getHeaders(name: String): Enumeration<String> {
        val values: MutableList<String> = Collections.list(super.getHeaders(name));
        if (customHeaders.containsKey(name)) {
            values.add(customHeaders[name]!!)
        }
        return Collections.enumeration(values);
    }

}
