// -----------------------------------------------------------------------------
// Copyright (c) Edifecs Inc. All Rights Reserved.
//
// This software is the confidential and proprietary information of Edifecs Inc.
// ("Confidential Information").  You shall not disclose such Confidential
// Information and shall use it only in accordance with the terms of the license
// agreement you entered into with Edifecs.
//
// EDIFECS MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
// SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
// WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
// NON-INFRINGEMENT. EDIFECS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
// LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR
// ITS DERIVATIVES.
// -----------------------------------------------------------------------------

/**
 * A Service is a bit of code that runs on the Application Platform. The annotations found in this package are to help
 * expose functionality and management options to the platform.<br/>
 * <br/>
 * The key area's of configurations available are:<br/>
 * - Service details and version information<br/>
 * - Dependencies required for the Service to run.<br/>
 * - Configuration properties the service needs.<br/>
 * - Command Handlers associated with the service<br/>
 * - Service Requirements<br/>
 * - Service Guarantee Information<br/>
 * - Monitoring Concerns<br/>
 * - Routing Concerns<br/>
 * <br/>
 * The Majority of these configurations are used at build time to compile default configurations for the application or
 * module. Some of these can be changed during installation and runtime.
 *
 * @author willclem
 */
package com.edifecs.servicemanager.annotations;