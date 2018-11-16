/*
 * Copyright 2018 Heiko Scherrer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openwms.common.transport.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * A TransportUnitApi.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
//@FeignClient("${owms.common-service.protocol}://${owms.common-service.name}")
public interface TransportUnitApi {

    @GetMapping(params = {"bk"})
    @ResponseBody
    TransportUnitVO getTransportUnit(@RequestParam("bk") String transportUnitBK);

    @GetMapping(params = {"actualLocation"})
    List<TransportUnitVO> getTransportUnitsOn(@RequestParam("actualLocation") String actualLocation);

    @PostMapping(params = {"bk"})
    @ResponseBody
    void createTU(@RequestParam("bk") String transportUnitBK, @RequestBody TransportUnitVO tu, @RequestParam(value = "strict", required = false) Boolean strict, HttpServletRequest req);

    @PostMapping(params = {"bk"})
    @ResponseBody
    void createTU(@RequestParam("bk") String transportUnitBK, @RequestParam("actualLocation") String actualLocation, @RequestParam("tut") String tut, @RequestParam(value = "strict", required = false) Boolean strict, HttpServletRequest req);

    @PutMapping(params = {"bk"})
    @ResponseBody
    TransportUnitVO updateTU(@RequestParam("bk") String transportUnitBK, @RequestBody TransportUnitVO tu);

    @PatchMapping(params = {"bk", "newLocation"})
    @ResponseBody
    TransportUnitVO moveTU(@RequestParam("bk") String transportUnitBK, @RequestParam("newLocation") String newLocation);
}
