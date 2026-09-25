const BASE=import.meta.env.VITE_API_URL||"/api";
export async function api(path,options={}){
 const token=localStorage.getItem("token");
 const headers={...(options.body instanceof FormData?{}:{"Content-Type":"application/json"}),...(token?{Authorization:"Bearer "+token}:{}),...(options.headers||{})};
 const res=await fetch(BASE+path,{...options,headers});
 const data=await res.json().catch(()=>({message:res.statusText}));
 if(!res.ok) throw new Error(data.message||"Request failed");
 return data;
}
