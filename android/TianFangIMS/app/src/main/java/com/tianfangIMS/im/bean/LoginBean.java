package com.tianfangIMS.im.bean;

import java.io.Serializable;

/**
 * Created by LianMengYu on 2017/1/16.
 */

public class LoginBean implements Serializable {


    private int code;
    private Text text;

    public LoginBean(int code, Text text) {
        this.code = code;
        this.text = text;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Text getText() {
        return text;
    }

    public void setText(Text text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "LoginBean{" +
                "code=" + code +
                ", text=" + text +
                '}';
    }

    public static class Text implements Serializable {
        private String account;
        private String address;
        private String birthday;
        private String email;
        private String fullname;
        private String groupmax;
        private String groupuse;
        private String id;
        private String intro;
        private String logo;
        private String mobile;
        private String password;
        private String pinyin;
        private String sex;
        private String telephone;
        private String workno;
        private String token;
        private priv priv;

        public Text(String account, String address, String birthday, String email, String fullname, String groupmax, String groupuse, String id, String intro, String logo, String mobile, String password, String pinyin, Text.priv priv, String sex, String telephone, String token, String workno) {
            this.account = account;
            this.address = address;
            this.birthday = birthday;
            this.email = email;
            this.fullname = fullname;
            this.groupmax = groupmax;
            this.groupuse = groupuse;
            this.id = id;
            this.intro = intro;
            this.logo = logo;
            this.mobile = mobile;
            this.password = password;
            this.pinyin = pinyin;
            this.priv = priv;
            this.sex = sex;
            this.telephone = telephone;
            this.token = token;
            this.workno = workno;
        }

        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getFullname() {
            return fullname;
        }

        public void setFullname(String fullname) {
            this.fullname = fullname;
        }

        public String getGroupmax() {
            return groupmax;
        }

        public void setGroupmax(String groupmax) {
            this.groupmax = groupmax;
        }

        public String getGroupuse() {
            return groupuse;
        }

        public void setGroupuse(String groupuse) {
            this.groupuse = groupuse;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getIntro() {
            return intro;
        }

        public void setIntro(String intro) {
            this.intro = intro;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getPinyin() {
            return pinyin;
        }

        public void setPinyin(String pinyin) {
            this.pinyin = pinyin;
        }

        public Text.priv getPriv() {
            return priv;
        }

        public void setPriv(Text.priv priv) {
            this.priv = priv;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getTelephone() {
            return telephone;
        }

        public void setTelephone(String telephone) {
            this.telephone = telephone;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getWorkno() {
            return workno;
        }

        public void setWorkno(String workno) {
            this.workno = workno;
        }

        @Override
        public String toString() {
            return "Text{" +
                    "account='" + account + '\'' +
                    ", address='" + address + '\'' +
                    ", birthday='" + birthday + '\'' +
                    ", email='" + email + '\'' +
                    ", fullname='" + fullname + '\'' +
                    ", groupmax='" + groupmax + '\'' +
                    ", groupuse='" + groupuse + '\'' +
                    ", id='" + id + '\'' +
                    ", intro='" + intro + '\'' +
                    ", logo='" + logo + '\'' +
                    ", mobile='" + mobile + '\'' +
                    ", password='" + password + '\'' +
                    ", pinyin='" + pinyin + '\'' +
                    ", sex='" + sex + '\'' +
                    ", telephone='" + telephone + '\'' +
                    ", workno='" + workno + '\'' +
                    ", token='" + token + '\'' +
                    ", priv=" + priv +
                    '}';
        }

        private static class priv implements Serializable {
            private Map map;

            public priv(Map map) {
                this.map = map;
            }

            public Map getMap() {
                return map;
            }

            public void setMap(Map map) {
                this.map = map;
            }

            @Override
            public String toString() {
                return "priv{" +
                        "map=" + map +
                        '}';
            }

            private static class Map implements Serializable {
                private String yyapppcd;
                private String rsglsc;
                private String stsz;
                private String cjxz;
                private String qzgl;
                private String rsgljcxx;
                private String qz;
                private String dpjhzsjbmkf;
                private String rsglck;
                private String bmglsc;
                private String qt;
                private String bmglxg;
                private String zzxxglck;
                private String qxgl;
                private String zzxxglxg;
                private String rsglxgmm;
                private String zzxxgl;
                private String rsgltj;
                private String bmglck;
                private String qzgljs;
                private String qzglxg;
                private String rsglyd;
                private String htgl;
                private String bmglyd;
                private String bmgl;
                private String qzglck;
                private String rsgl;
                private String djj;
                private String bmgltj;
                private String grsz;

                public Map(String bmgl, String bmglck, String bmglsc, String bmgltj, String bmglxg, String bmglyd, String cjxz, String djj, String dpjhzsjbmkf, String grsz, String htgl, String qt, String qxgl, String qz, String qzgl, String qzglck, String qzgljs, String qzglxg, String rsgl, String rsglck, String rsgljcxx, String rsglsc, String rsgltj, String rsglxgmm, String rsglyd, String stsz, String yyapppcd, String zzxxgl, String zzxxglck, String zzxxglxg) {
                    this.bmgl = bmgl;
                    this.bmglck = bmglck;
                    this.bmglsc = bmglsc;
                    this.bmgltj = bmgltj;
                    this.bmglxg = bmglxg;
                    this.bmglyd = bmglyd;
                    this.cjxz = cjxz;
                    this.djj = djj;
                    this.dpjhzsjbmkf = dpjhzsjbmkf;
                    this.grsz = grsz;
                    this.htgl = htgl;
                    this.qt = qt;
                    this.qxgl = qxgl;
                    this.qz = qz;
                    this.qzgl = qzgl;
                    this.qzglck = qzglck;
                    this.qzgljs = qzgljs;
                    this.qzglxg = qzglxg;
                    this.rsgl = rsgl;
                    this.rsglck = rsglck;
                    this.rsgljcxx = rsgljcxx;
                    this.rsglsc = rsglsc;
                    this.rsgltj = rsgltj;
                    this.rsglxgmm = rsglxgmm;
                    this.rsglyd = rsglyd;
                    this.stsz = stsz;
                    this.yyapppcd = yyapppcd;
                    this.zzxxgl = zzxxgl;
                    this.zzxxglck = zzxxglck;
                    this.zzxxglxg = zzxxglxg;
                }

                public String getBmgl() {
                    return bmgl;
                }

                public void setBmgl(String bmgl) {
                    this.bmgl = bmgl;
                }

                public String getBmglck() {
                    return bmglck;
                }

                public void setBmglck(String bmglck) {
                    this.bmglck = bmglck;
                }

                public String getBmglsc() {
                    return bmglsc;
                }

                public void setBmglsc(String bmglsc) {
                    this.bmglsc = bmglsc;
                }

                public String getBmgltj() {
                    return bmgltj;
                }

                public void setBmgltj(String bmgltj) {
                    this.bmgltj = bmgltj;
                }

                public String getBmglxg() {
                    return bmglxg;
                }

                public void setBmglxg(String bmglxg) {
                    this.bmglxg = bmglxg;
                }

                public String getBmglyd() {
                    return bmglyd;
                }

                public void setBmglyd(String bmglyd) {
                    this.bmglyd = bmglyd;
                }

                public String getCjxz() {
                    return cjxz;
                }

                public void setCjxz(String cjxz) {
                    this.cjxz = cjxz;
                }

                public String getDjj() {
                    return djj;
                }

                public void setDjj(String djj) {
                    this.djj = djj;
                }

                public String getDpjhzsjbmkf() {
                    return dpjhzsjbmkf;
                }

                public void setDpjhzsjbmkf(String dpjhzsjbmkf) {
                    this.dpjhzsjbmkf = dpjhzsjbmkf;
                }

                public String getGrsz() {
                    return grsz;
                }

                public void setGrsz(String grsz) {
                    this.grsz = grsz;
                }

                public String getHtgl() {
                    return htgl;
                }

                public void setHtgl(String htgl) {
                    this.htgl = htgl;
                }

                public String getQt() {
                    return qt;
                }

                public void setQt(String qt) {
                    this.qt = qt;
                }

                public String getQxgl() {
                    return qxgl;
                }

                public void setQxgl(String qxgl) {
                    this.qxgl = qxgl;
                }

                public String getQz() {
                    return qz;
                }

                public void setQz(String qz) {
                    this.qz = qz;
                }

                public String getQzgl() {
                    return qzgl;
                }

                public void setQzgl(String qzgl) {
                    this.qzgl = qzgl;
                }

                public String getQzglck() {
                    return qzglck;
                }

                public void setQzglck(String qzglck) {
                    this.qzglck = qzglck;
                }

                public String getQzgljs() {
                    return qzgljs;
                }

                public void setQzgljs(String qzgljs) {
                    this.qzgljs = qzgljs;
                }

                public String getQzglxg() {
                    return qzglxg;
                }

                public void setQzglxg(String qzglxg) {
                    this.qzglxg = qzglxg;
                }

                public String getRsgl() {
                    return rsgl;
                }

                public void setRsgl(String rsgl) {
                    this.rsgl = rsgl;
                }

                public String getRsglck() {
                    return rsglck;
                }

                public void setRsglck(String rsglck) {
                    this.rsglck = rsglck;
                }

                public String getRsgljcxx() {
                    return rsgljcxx;
                }

                public void setRsgljcxx(String rsgljcxx) {
                    this.rsgljcxx = rsgljcxx;
                }

                public String getRsglsc() {
                    return rsglsc;
                }

                public void setRsglsc(String rsglsc) {
                    this.rsglsc = rsglsc;
                }

                public String getRsgltj() {
                    return rsgltj;
                }

                public void setRsgltj(String rsgltj) {
                    this.rsgltj = rsgltj;
                }

                public String getRsglxgmm() {
                    return rsglxgmm;
                }

                public void setRsglxgmm(String rsglxgmm) {
                    this.rsglxgmm = rsglxgmm;
                }

                public String getRsglyd() {
                    return rsglyd;
                }

                public void setRsglyd(String rsglyd) {
                    this.rsglyd = rsglyd;
                }

                public String getStsz() {
                    return stsz;
                }

                public void setStsz(String stsz) {
                    this.stsz = stsz;
                }

                public String getYyapppcd() {
                    return yyapppcd;
                }

                public void setYyapppcd(String yyapppcd) {
                    this.yyapppcd = yyapppcd;
                }

                public String getZzxxgl() {
                    return zzxxgl;
                }

                public void setZzxxgl(String zzxxgl) {
                    this.zzxxgl = zzxxgl;
                }

                public String getZzxxglck() {
                    return zzxxglck;
                }

                public void setZzxxglck(String zzxxglck) {
                    this.zzxxglck = zzxxglck;
                }

                public String getZzxxglxg() {
                    return zzxxglxg;
                }

                public void setZzxxglxg(String zzxxglxg) {
                    this.zzxxglxg = zzxxglxg;
                }

                @Override
                public String toString() {
                    return "Map{" +
                            "bmgl='" + bmgl + '\'' +
                            ", yyapppcd='" + yyapppcd + '\'' +
                            ", rsglsc='" + rsglsc + '\'' +
                            ", stsz='" + stsz + '\'' +
                            ", cjxz='" + cjxz + '\'' +
                            ", qzgl='" + qzgl + '\'' +
                            ", rsgljcxx='" + rsgljcxx + '\'' +
                            ", qz='" + qz + '\'' +
                            ", dpjhzsjbmkf='" + dpjhzsjbmkf + '\'' +
                            ", rsglck='" + rsglck + '\'' +
                            ", bmglsc='" + bmglsc + '\'' +
                            ", qt='" + qt + '\'' +
                            ", bmglxg='" + bmglxg + '\'' +
                            ", zzxxglck='" + zzxxglck + '\'' +
                            ", qxgl='" + qxgl + '\'' +
                            ", zzxxglxg='" + zzxxglxg + '\'' +
                            ", rsglxgmm='" + rsglxgmm + '\'' +
                            ", zzxxgl='" + zzxxgl + '\'' +
                            ", rsgltj='" + rsgltj + '\'' +
                            ", bmglck='" + bmglck + '\'' +
                            ", qzgljs='" + qzgljs + '\'' +
                            ", qzglxg='" + qzglxg + '\'' +
                            ", rsglyd='" + rsglyd + '\'' +
                            ", htgl='" + htgl + '\'' +
                            ", bmglyd='" + bmglyd + '\'' +
                            ", qzglck='" + qzglck + '\'' +
                            ", rsgl='" + rsgl + '\'' +
                            ", djj='" + djj + '\'' +
                            ", bmgltj='" + bmgltj + '\'' +
                            ", grsz='" + grsz + '\'' +
                            '}';
                }
            }
        }


    }

}
