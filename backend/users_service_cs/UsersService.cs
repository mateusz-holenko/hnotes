using Microsoft.EntityFrameworkCore;

public class UsersDbContext : DbContext
{
    public UsersDbContext(DbContextOptions<UsersDbContext> options) : base(options) {}

    public DbSet<User> Users { get; set; }
}

public class User
{
    public User(int id, string username)
    {
        Id = id;
        Username = username;
    }

    public int Id { get; set; }
    public string Username { get; set; }
}

public class UserCredentials
{
    public string? username { get; set; }
    public string? password { get; set; }
}

public static class UsersService
{
    private static List<User> users = new List<User>() {
        new User(100, "admin"),
        new User(101, "User1"),
        new User(800, "Guest")
    };

    public static void Main(string[] args)
    {
        var builder = WebApplication.CreateBuilder(args);
        builder.Services.AddControllers();

        var dbConnectionString = "server=localhost;user=houen;password=houen;database=usersdatabase";
        builder.Services.AddDbContext<UsersDbContext>(options => options
            .UseMySql(dbConnectionString, new MariaDbServerVersion("11.7.2")));

        var app = builder.Build();

        using(var scope = app.Services.CreateScope())
        {
            var dbc = scope.ServiceProvider.GetService<UsersDbContext>();

            foreach(var user in users)
            {
                if(!dbc.Users.Contains(user))
                {
                    dbc.Users.Add(user);
                }
            }
            dbc.SaveChanges();
        }

        app.MapControllers();
        app.Run();
    }
}
