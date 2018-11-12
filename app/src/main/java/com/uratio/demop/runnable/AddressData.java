package com.uratio.demop.runnable;

import java.io.Serializable;
import java.util.ArrayList;

public class AddressData implements Serializable {
    private int id;
    private String title;
    private int pid;
    private int position;
    private ArrayList<ChildrenCity> children;

    @Override
    public String toString() {
        return "AddressData{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", pid=" + pid +
                ", position=" + position +
                ", children=" + children +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public ArrayList<ChildrenCity> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<ChildrenCity> children) {
        this.children = children;
    }

    public static class ChildrenCity implements Serializable{
        /**
         * id : 52
         * title : 北京
         * pid : 2
         * children : [{"id":500,"title":"东城区","pid":52,"children":[]},{"id":501,"title":"西城区","pid":52,"children":[]},{"id":502,"title":"海淀区",
         * "pid":52,"children":[]},{"id":503,"title":"朝阳区","pid":52,"children":[]},{"id":504,"title":"崇文区","pid":52,"children":[]},{"id":505,
         * "title":"宣武区","pid":52,"children":[]},{"id":506,"title":"丰台区","pid":52,"children":[]},{"id":507,"title":"石景山区","pid":52,"children":[]},
         * {"id":508,"title":"房山区","pid":52,"children":[]},{"id":509,"title":"门头沟区","pid":52,"children":[]},{"id":510,"title":"通州区","pid":52,
         * "children":[]},{"id":511,"title":"顺义区","pid":52,"children":[]},{"id":512,"title":"昌平区","pid":52,"children":[]},{"id":513,"title":"怀柔区",
         * "pid":52,"children":[]},{"id":514,"title":"平谷区","pid":52,"children":[]},{"id":515,"title":"大兴区","pid":52,"children":[]},{"id":3447,
         * "title":"亦庄区","pid":52,"children":[]}]
         */

        private int id;
        private String title;
        private int pid;
        private int position;
        private ArrayList<ChildrenCounty> children;

        @Override
        public String toString() {
            return "ChildrenCity{" +
                    "id=" + id +
                    ", title='" + title + '\'' +
                    ", pid=" + pid +
                    ", position=" + position +
                    ", children=" + children +
                    '}';
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getPid() {
            return pid;
        }

        public void setPid(int pid) {
            this.pid = pid;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public ArrayList<ChildrenCounty> getChildren() {
            return children;
        }

        public void setChildren(ArrayList<ChildrenCounty> children) {
            this.children = children;
        }

        public static class ChildrenCounty implements Serializable{
            /**
             * id : 500
             * title : 东城区
             * pid : 52
             * children : []
             */

            private int id;
            private String title;
            private int pid;
            private int position;
            private ArrayList<ChildrenStreet> children;

            @Override
            public String toString() {
                return "ChildrenCounty{" +
                        "id=" + id +
                        ", title='" + title + '\'' +
                        ", pid=" + pid +
                        ", position=" + position +
                        ", children=" + children +
                        '}';
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public int getPid() {
                return pid;
            }

            public void setPid(int pid) {
                this.pid = pid;
            }

            public int getPosition() {
                return position;
            }

            public void setPosition(int position) {
                this.position = position;
            }

            public ArrayList<ChildrenStreet> getChildren() {
                return children;
            }

            public void setChildren(ArrayList<ChildrenStreet> children) {
                this.children = children;
            }

            public static class ChildrenStreet implements Serializable{
                /**
                 * id : 500
                 * title : 东城区
                 * pid : 52
                 * children : []
                 */

                private int id;
                private String title;
                private int pid;
                private int position;

                @Override
                public String toString() {
                    return "ChildrenStreet{" +
                            "id=" + id +
                            ", title='" + title + '\'' +
                            ", pid=" + pid +
                            ", position=" + position +
                            '}';
                }

                public int getPosition() {
                    return position;
                }

                public void setPosition(int position) {
                    this.position = position;
                }

                public int getId() {
                    return id;
                }

                public void setId(int id) {
                    this.id = id;
                }

                public String getTitle() {
                    return title;
                }

                public void setTitle(String title) {
                    this.title = title;
                }

                public int getPid() {
                    return pid;
                }

                public void setPid(int pid) {
                    this.pid = pid;
                }
            }
        }
    }
}
