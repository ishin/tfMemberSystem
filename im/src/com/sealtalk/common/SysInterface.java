package com.sealtalk.common;

public enum SysInterface {
	GETONEOFMEMBER("abmember!getOneOfMemberAb"),
	SEARCHUSER("abmember!searchUserAb"),
	UPDATEMEMWEB("abmember!updateMemberInfoForWebAb"),
	UPDATEMEMAPP("abmember!updateMemberInfoForAppAb"),
	ALLMEMBER("abmember!getAllMemberInfoAb"),
	MEMBERONLINE("abmember!getAllMemberOnLineStatusAb"),
	MULTIPLEMEMBER("abmember!getMultipleMemberForAccountsAb"),
	COUNTMEMBER("abmember!getMemberCountAb"),
	CHECKACCOUNT("abmember!checkAccountAb"),
	UPDATETOKEN("abmember!updateUserTokenForIdAb"),
	GETTEXTCODE("abmember!getTextCodeAb"),
	SAVETEXTCODE("abmember!saveTextCodeAb"),
	MEMBERIDSBYACCOUNT("abmember!getMemberIdsByAccountAb"),
	VALIDEOLDPWD("abmember!valideOldPwdAb"),
	UPDATEPWDACCOUT("abmember!updateUserPwdForAccountAb"),
	UPDATEPWDPHONE("abmember!updateUserPwdForPhoneAb"),
	SAVEPIC("abmember!saveSelectedPicAb"),
	SAVETEMPPIC("abmember!saveTempPicAb"),
	DELUSERLOGS("abmember!delUserLogosAb"),	
	ISUSEDPIC("abmember!isUsedPicAb"),
	GETUSERLOGOS("abmember!getUserLogosAb"),
	GETMIDFORAC("abmember!getMemberIdForAccountAb"),
	MULTIPLEMEMBERFORID("abmember!getMultipleMemberForIdsAb"),
	MEMBERFORID("abmember!getMemberForIdAb"),
	LIMITMEMBERIDS("abmember!getLimitMemberIdsAb"),
	MEMBERBYTOKEN("abmember!getMemberByTokenAb"),
	GETMEMBERPARAM("abmember!getMemberParamAb"),
	GETMEMBERBYACCOUNT("abmember!getMemberByAccountAb"),
	BRANCHMEMBERIDS("abranch!getBranchMemberByMemberIdsAb"),
	GETINFOS("abranch!getInfosAb"),
	GETPOSITION("abranch!getPositionAb"),
	BRANCHMEMBER("abranch!getBranchTreeAndMemberAb"),
	MEMBEROFBRANCH("abranch!getBranchMemberAb"),
	BRANCHTREE("abranch!getBranchTreeAb"),
	GETORGANCODE("abranch!getOrganCodeAb"),
	GETPRIVILEGE("abprivilege!getRoleIdForIdAb"),
	INITLOGINPRIV("abprivilege!getInitLoginPrivAb"),
	GETPRIVBYURL("abprivilege!getPrivByUrlAb"),
	GETPRIVBYPRIVS("abprivilege!getRolePrivsByPrivsAb"),
	GETROLEMEMBERBYROLEIDS("abprivilege!getMemberRolesByRoleIdsAb"),
	PRIVBYMEMBER("abprivilege!getPrivStringByMemberAb"),
	VALIDAPPSECRET("abauth!validAppIdAndSecretAb");

	private String name;
	
	private SysInterface(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
