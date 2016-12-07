using UnityEngine;
using System.Collections;

public class TowerScript : MonoBehaviour {

    public Sprite[] look;
    public GameObject[] ammo;

    public float[] shooting_rate;
    public float[] damage;
    public float[] velocity;
    public float[] shooting_range;


    public GameObject target = null;
    private int lvl = 0;
    private float curr_shoot_delay = 0;


    void Start () {
        curr_shoot_delay = 0;
        gameObject.GetComponent<SpriteRenderer>().sprite = look[lvl];
        target = null;

	}
	
	void FixedUpdate () {
        //shoot only if there is target and we are ready
        if (curr_shoot_delay > 0)
        {
            curr_shoot_delay -= Time.deltaTime;
        } else if(target)
        {
            
            shoot();
            curr_shoot_delay = shooting_rate[lvl];
        }

        if (!target)
        {
            aim();
        }
        else if (dist(target) > shooting_range[lvl])
        {
            target = null;
        }
        else { rt(); }   	
	}

    void shoot(){
        var shot = Instantiate(ammo[lvl]) as GameObject;
        shot.GetComponent<Transform>().position = gameObject.GetComponent<Transform>().position;
        shot.GetComponent<AmmoScript>().target = target;
        shot.GetComponent<AmmoScript>().damage = damage[lvl];
        shot.GetComponent<AmmoScript>().speed = velocity[lvl];
    }

    void aim()
    {
        var spwn = GameObject.Find("SPAWNER");
        ArrayList mnstrs = spwn.GetComponent<SpawnerScript>().monsters_list;


        float min_distance = 1.1f * shooting_range[lvl];
        target = null;
        foreach(GameObject gameObj in mnstrs) {
            float d = dist(gameObj);
            if (d < min_distance)
            {
                min_distance = d;
                target = gameObj;
            }
        }
        
    }

    float dist(GameObject obj)
    {
        Vector2 v_1 = obj.GetComponent<Transform>().position;
        Vector2 v_2 = gameObject.GetComponent<Transform>().position;

        float d = Mathf.Sqrt(Mathf.Pow(v_1.x - v_2.x, 2) + Mathf.Pow(v_1.y - v_2.y, 2));
        return d;        
    }

    void rt() //rotate
    {
        float y1, y2;
        float x1, x2;
        x1 = target.GetComponent<Transform>().position.x;
        x2 = gameObject.GetComponent<Transform>().position.x;
        y1 = target.GetComponent<Transform>().position.y;
        y2 = gameObject.GetComponent<Transform>().position.y;
        gameObject.GetComponent<Transform>().Rotate(0,0,10);
        if(gameObject.GetComponent<Transform>().rotation.z >= Mathf.Atan((y1 - y2)/(x2 - x1)))
        {
        gameObject.GetComponent<Transform>().Rotate(0,0,10);
        }
        ////hz

    }
 
}
